/*
 * The MIT License
 *
 * Copyright 2015 Shekhar Gulati <shekhargulati84@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.reactivex.docker.client;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.ResponseBody;
import io.reactivex.docker.client.function.BufferTransformer;
import io.reactivex.docker.client.function.StringResponseToCollectionTransformer;
import io.reactivex.docker.client.function.StringResponseTransformer;
import io.reactivex.docker.client.http_client.HttpStatus;
import io.reactivex.docker.client.http_client.HttpStatusBufferSubscriber;
import io.reactivex.docker.client.http_client.RestServiceCommunicationException;
import io.reactivex.docker.client.http_client.RxHttpClient;
import io.reactivex.docker.client.representations.*;
import io.reactivex.docker.client.utils.Strings;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.gson.FieldNamingPolicy.UPPER_CAMEL_CASE;
import static io.reactivex.docker.client.ContainerLogQueryParameters.withDefaultValues;
import static io.reactivex.docker.client.ImageListQueryParameters.allImagesQueryParameters;
import static io.reactivex.docker.client.ImageListQueryParameters.queryParameterWithImageName;
import static io.reactivex.docker.client.QueryParametersBuilder.defaultQueryParameters;
import static io.reactivex.docker.client.function.ResponseTransformer.httpStatus;
import static io.reactivex.docker.client.utils.Dates.DOCKER_DATE_TIME_FORMAT;
import static io.reactivex.docker.client.utils.StreamUtils.iteratorToStream;
import static io.reactivex.docker.client.utils.Validations.validate;

class RxDockerClient implements DockerClient {

    private static final String EMPTY_BODY = "";

    private final Logger logger = LoggerFactory.getLogger(RxDockerClient.class);
    private final String apiUri;
    private final RxHttpClient httpClient;

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(UPPER_CAMEL_CASE)
            .setDateFormat(DOCKER_DATE_TIME_FORMAT)
            .setPrettyPrinting().create();

    RxDockerClient(final String dockerHost, final String dockerCertPath) {
        this(Optional.ofNullable(dockerHost), Optional.ofNullable(dockerCertPath));
    }

    private RxDockerClient(final Optional<String> dockerHost, final Optional<String> dockerCertPath) {
        final HostAndPort hostAndPort = dockerHost.map(HostAndPort::from).orElse(HostAndPort.using(DEFAULT_DOCKER_HOST, DEFAULT_DOCKER_PORT));
        final String scheme = dockerCertPath.isPresent() ? "https" : "http";

        apiUri = scheme + "://" + hostAndPort.getHost() + ":" + hostAndPort.getPort();
        logger.info("Docker API uri {}", apiUri);
        httpClient = RxHttpClient.newRxClient(hostAndPort.getHost(), hostAndPort.getPort(), dockerCertPath);
    }

    @Override
    public String getApiUri() {
        return apiUri;
    }

    // Misc operations
    @Override
    public Observable<DockerVersion> serverVersionObs() {
        return httpClient
                .get(VERSION_ENDPOINT,
                        (StringResponseTransformer<DockerVersion>) json -> gson.fromJson(json, DockerVersion.class));
    }

    @Override
    public DockerVersion serverVersion() {
        return serverVersionObs().
                toBlocking().
                single();
    }

    @Override
    public Observable<DockerInfo> infoObs() {
        return httpClient
                .get(INFO_ENDPOINT,
                        (StringResponseTransformer<DockerInfo>) json -> gson.fromJson(json, DockerInfo.class));
    }

    @Override
    public DockerInfo info() {
        return infoObs().
                toBlocking().
                single();
    }

    @Override
    public HttpStatus checkAuth(final AuthConfig authConfig) {
        return checkAuthObs(authConfig).onErrorReturn(e -> {
            if (e instanceof RestServiceCommunicationException) {
                logger.info("checkAuth threw RestServiceCommunicationException");
                RestServiceCommunicationException restException = (RestServiceCommunicationException) e;
                return HttpStatus.of(restException.getCode(), restException.getHttpMessage());
            }
            return HttpStatus.of(500, e.getMessage());
        }).toBlocking().last();
    }

    @Override
    public Observable<HttpStatus> checkAuthObs(final AuthConfig authConfig) {
        validate(authConfig, cfg -> cfg == null, () -> "authConfig can't be null.");
        final String endpoint = CHECK_AUTH_ENDPOINT;
        return httpClient.post(endpoint, authConfig.toJson());
    }

    @Override
    public HttpStatus ping() {
        final String endpoint = PING_ENDPOINT;
        return httpClient.getResponseHttpStatus(endpoint).onErrorReturn(e -> {
            if (e instanceof RestServiceCommunicationException) {
                logger.info("checkAuth threw RestServiceCommunicationException");
                RestServiceCommunicationException restException = (RestServiceCommunicationException) e;
                return HttpStatus.of(restException.getCode(), restException.getHttpMessage());
            }
            return HttpStatus.of(500, e.getMessage());
        }).toBlocking().last();
    }

    // Container operations
    @Override
    public Observable<DockerContainer> listRunningContainerObs() {
        return listContainersObs(defaultQueryParameters());
    }

    @Override
    public List<DockerContainer> listRunningContainers() {
        return listRunningContainerObs().toList().toBlocking().single();
    }

    @Override
    public Observable<DockerContainer> listAllContainersObs() {
        return listContainersObs(new QueryParametersBuilder().withAll(true).createQueryParameters());
    }

    @Override
    public List<DockerContainer> listAllContainers() {
        return listAllContainersObs().toList().toBlocking().single();
    }

    @Override
    public List<DockerContainer> listContainers(QueryParameters queryParameters) {
        return listContainersObs(queryParameters).toList().toBlocking().single();
    }

    @Override
    public Observable<DockerContainer> listContainersObs(QueryParameters queryParameters) {
        final String query = queryParameters.toQuery();
        final String endpoint = String.format(CONTAINER_ENDPOINT, query);
        return httpClient.get(endpoint,
                (StringResponseToCollectionTransformer<DockerContainer>) json -> gson.fromJson(json, new TypeToken<List<DockerContainer>>() {
                }.getType()));
    }

    @Override
    public DockerContainerResponse createContainer(final DockerContainerRequest request) {
        return createContainer(request, null);
    }

    @Override
    public DockerContainerResponse createContainer(final DockerContainerRequest request, final String name) {
        return createContainerObs(request, Optional.ofNullable(name)).toBlocking().single();
    }

    @Override
    public Observable<DockerContainerResponse> createContainerObs(final DockerContainerRequest request, final Optional<String> name) {
        String content = request.toJson();
        logger.info("Creating container for json request >>\n'{}'", content);
        final String uri = name.isPresent() ? CREATE_CONTAINER_ENDPOINT + "?name=" + name.get() : CREATE_CONTAINER_ENDPOINT;
        return httpClient.post(uri, content, (ResponseBody body) -> gson.fromJson(body.string(), DockerContainerResponse.class));
    }

    @Override
    public ContainerInspectResponse inspectContainer(final String containerId) {
        return inspectContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<ContainerInspectResponse> inspectContainerObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_JSON_ENDPOINT, containerId);
        return httpClient
                .get(uri,
                        (StringResponseTransformer<ContainerInspectResponse>) json -> gson.fromJson(json, ContainerInspectResponse.class));
    }

    @Override
    public ProcessListResponse listProcesses(final String containerId) {
        return listProcessesObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<ProcessListResponse> listProcessesObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_LIST_PROCESS_ENDPOINT, containerId);
        return httpClient
                .get(uri,
                        (StringResponseTransformer<ProcessListResponse>) json -> gson.fromJson(json, ProcessListResponse.class));
    }

    @Override
    public HttpStatus startContainer(final String containerId) {
        return startContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> startContainerObs(final String containerId) {
        final String uri = String.format(CONTAINER_START_ENDPOINT, containerId);
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public HttpStatus stopContainer(final String containerId, final int waitInSecs) {
        return stopContainerObs(containerId, waitInSecs).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> stopContainerObs(final String containerId, final int waitInSecs) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_STOP_ENDPOINT, containerId) + "?t=" + waitInSecs;
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public HttpStatus restartContainer(final String containerId, final int waitInSecs) {
        return restartContainerObs(containerId, waitInSecs).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> restartContainerObs(final String containerId, final int waitInSecs) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_RESTART_ENDPOINT, containerId) + "?t=" + waitInSecs;
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public HttpStatus killRunningContainer(final String containerId) {
        return killRunningContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> killRunningContainerObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_KILL_ENDPOINT, containerId);
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public HttpStatus removeContainer(final String containerId) {
        return removeContainer(containerId, false, false);
    }

    @Override
    public HttpStatus removeContainer(final String containerId, final boolean removeVolume, final boolean force) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        return removeContainerObs(containerId, removeVolume, force).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> removeContainerObs(final String containerId) {
        return removeContainerObs(containerId, false, false);
    }

    @Override
    public Observable<HttpStatus> removeContainerObs(final String containerId, final boolean removeVolume, final boolean force) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_REMOVE_ENDPOINT, containerId) + "?v=" + removeVolume + "&force=" + force;
        return httpClient.delete(uri);
    }

    @Override
    public HttpStatus renameContainer(final String containerId, final String newName) {
        return renameContainerObs(containerId, newName).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> renameContainerObs(final String containerId, final String newName) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        validate(newName, Strings::isEmptyOrNull, () -> "Please provide newName that you want't to use for container.");
        final String uri = String.format(CONTAINER_RENAME_ENDPOINT, containerId) + "?name=" + newName;
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public HttpStatus waitContainer(final String containerId) {
        return waitContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> waitContainerObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_WAIT_ENDPOINT, containerId);
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public void exportContainer(final String containerId, final Path pathToExportTo) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpointUri = String.format(CONTAINER_EXPORT_ENDPOINT, containerId);
        Observable<Buffer> bufferStream = httpClient.getResponseBuffer(endpointUri);

        String exportFilePath = pathToExportTo.toString() + "/" + containerId + ".tar";
        try (FileOutputStream out = new FileOutputStream(exportFilePath)) {
            Subscriber<Buffer> httpSubscriber = new Subscriber<Buffer>() {
                @Override
                public void onCompleted() {
                    logger.info("Exported container to path {}", exportFilePath);
                }

                @Override
                public void onError(Throwable e) {
                    logger.error("Error encountered >> ", e);
                }

                @Override
                public void onNext(Buffer res) {
                    try {
                        logger.info("Exporting to path {}", exportFilePath);
                        final byte[] buffer = new byte[1024];
                        int n;
                        while (-1 != (n = res.read(buffer))) {
                            out.write(buffer, 0, n);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            bufferStream.subscribe(httpSubscriber);
            httpSubscriber.unsubscribe();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Observable<ContainerStats> containerStatsObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpointUri = String.format(CONTAINER_STATS_ENDPOINT, containerId);
        return httpClient.get(endpointUri,
                (BufferTransformer<ContainerStats>) buffer -> gson.fromJson(buffer.readUtf8(), ContainerStats.class));
    }

    @Override
    public Observable<String> containerLogsObs(final String containerId) {
        return containerLogsObs(containerId, withDefaultValues());
    }

    @Override
    public Observable<String> containerLogsObs(final String containerId, ContainerLogQueryParameters queryParameters) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpointUri = String.format(CONTAINER_LOGS_ENDPOINT, containerId) + queryParameters.toQueryParametersString();

        return httpClient
                .getResponseBuffer(endpointUri,
                        Stream.of(new SimpleEntry<>("Accept", "application/vnd.docker.raw-stream"))
                                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)))
                .map(buffer -> buffer.readString(Charset.defaultCharset()));
    }

    // Image Endpoint
    @Override
    public HttpStatus pullImage(final String fromImage) {
        return pullImage(fromImage, Optional.empty(), Optional.empty());
    }

    @Override
    public HttpStatus pullImage(final String fromImage, final String tag) {
        return pullImage(fromImage, null, tag);
    }

    @Override
    public HttpStatus pullImage(final String fromImage, final String user, final String tag) {
        return pullImage(fromImage, Optional.ofNullable(user), Optional.ofNullable(tag));
    }

    private HttpStatus pullImage(final String fromImage, final Optional<String> user, final Optional<String> tag) {
        Observable<Buffer> imageObs = pullImageObs(fromImage, user, tag);
        HttpStatusBufferSubscriber subscriber = new HttpStatusBufferSubscriber();
        imageObs.subscribe(subscriber);
        subscriber.unsubscribe();
        return subscriber.getStatus();
    }

    @Override
    public Observable<Buffer> pullImageObs(final String fromImage, final Optional<String> user, final Optional<String> tag) {
        validate(fromImage, Strings::isEmptyOrNull, () -> "fromImage can't be null or empty.");
        final String endpoint = String.format(IMAGE_CREATE_ENDPOINT, user.map(u -> u + "/").orElse(""), fromImage, tag.orElse("latest"));
        return httpClient.postAndReceiveResponseBuffer(endpoint);
    }

    @Override
    public Stream<DockerImage> listAllImages() {
        return listImages(allImagesQueryParameters());
    }

    @Override
    public Stream<DockerImage> listImages(final String imageName) {
        return listImages(queryParameterWithImageName(imageName));
    }

    @Override
    public Stream<DockerImage> listDanglingImages() {
        return listImages(ImageListQueryParameters.defaultQueryParameters().addFilter("dangling", "true"));
    }

    @Override
    public Stream<DockerImage> listImages() {
        return listImages(ImageListQueryParameters.defaultQueryParameters());
    }

    @Override
    public Stream<DockerImage> listImages(ImageListQueryParameters queryParameters) {
        return iteratorToStream(listImagesObs(queryParameters).toBlocking().getIterator());
    }

    @Override
    public Observable<DockerImage> listImagesObs(ImageListQueryParameters queryParameters) {
        final String endpoint = IMAGE_LIST_ENDPOINT + queryParameters.toQuery();
        return httpClient.get(endpoint,
                (StringResponseToCollectionTransformer<DockerImage>) json -> gson.fromJson(json, new TypeToken<List<DockerImage>>() {
                }.getType()));

    }

    @Override
    public HttpStatus removeImage(final String imageName, final boolean noPrune, final boolean force) {
        return removeImageObs(imageName, noPrune, force).toBlocking().last();
    }

    @Override
    public HttpStatus removeImage(final String imageName) {
        return removeImage(imageName, false, false);
    }

    @Override
    public Observable<HttpStatus> removeImageObs(final String imageName) {
        return removeImageObs(imageName, false, false);
    }

    @Override
    public Observable<HttpStatus> removeImageObs(final String imageName, final boolean noPrune, final boolean force) {
        validate(imageName, Strings::isEmptyOrNull, () -> "imageName can't be null or empty.");
        final String endpoint = String.format(IMAGE_REMOVE_ENDPOINT, imageName) + "?noprune=" + noPrune + "&force=" + force;
        return httpClient.delete(endpoint);
    }

    @Override
    public Stream<DockerImageInfo> searchImages(final String searchTerm, Predicate<DockerImageInfo> predicate) {
        return iteratorToStream(searchImagesObs(searchTerm, predicate).toBlocking().getIterator());
    }

    @Override
    public Observable<DockerImageInfo> searchImagesObs(final String searchTerm, Predicate<DockerImageInfo> predicate) {
        validate(searchTerm, Strings::isEmptyOrNull, () -> "searchTerm can't be null or empty.");
        final String endpoint = String.format("%s?term=%s", IMAGE_SEARCH_ENDPOINT, searchTerm);
        return httpClient.get(endpoint,
                (StringResponseToCollectionTransformer<DockerImageInfo>) json -> gson.fromJson(json, new TypeToken<List<DockerImageInfo>>() {
                }.getType())).filter(predicate::test);
    }

    @Override
    public Observable<String> buildImageObs(final String repositoryName, final Path pathToTarArchive) {
        return buildImageObs(repositoryName, pathToTarArchive, BuildImageQueryParameters.withDefaultValues());
    }

    @Override
    public Observable<String> buildImageObs(final String repositoryName, final Path pathToTarArchive, BuildImageQueryParameters queryParameters) {
        validate(pathToTarArchive, path -> path == null, () -> "path to archive can't be null");
        validate(pathToTarArchive, path -> !path.toFile().exists(), () -> String.format("%s can't be resolved to a tar file", pathToTarArchive.toAbsolutePath().toString()));
        final String endpoint = String.format("%s?t=%s", IMAGE_BUILD_ENDPOINT, repositoryName) + queryParameters.toQueryParameterString();
        return httpClient.postTarStream(endpoint, pathToTarArchive, buf -> buf.readString(Charset.defaultCharset()));
    }

    @Override
    public HttpStatus tagImage(final String image, final ImageTagQueryParameters queryParameters) {
        return tagImageObs(image, queryParameters).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> tagImageObs(final String image, final ImageTagQueryParameters queryParameters) {
        validate(image, Strings::isEmptyOrNull, () -> "image can't be null or empty.");
        final String endpoint = String.format(IMAGE_TAG_ENDPOINT, image) + queryParameters.toQuery();
        return httpClient.post(endpoint);
    }

    @Override
    public Stream<DockerImageHistory> imageHistory(final String image) {
        return iteratorToStream(imageHistoryObs(image).toBlocking().getIterator());
    }

    @Override
    public Observable<DockerImageHistory> imageHistoryObs(final String image) {
        validate(image, Strings::isEmptyOrNull, () -> "image can't be null or empty.");
        final String endpoint = String.format(IMAGE_HISTORY_ENDPOINT, image);
        return httpClient.get(endpoint,
                (StringResponseToCollectionTransformer<DockerImageHistory>) json -> gson.fromJson(json, new TypeToken<List<DockerImageHistory>>() {
                }.getType()));
    }

    @Override
    public DockerImageInspectDetails inspectImage(final String image) {
        return inspectImageObs(image).toBlocking().single();
    }

    @Override
    public Observable<DockerImageInspectDetails> inspectImageObs(final String image) {
        validate(image, Strings::isEmptyOrNull, () -> "image can't be null or empty.");
        final String endpoint = String.format(IMAGE_INSPECT_ENDPOINT, image);
        return httpClient.get(endpoint,
                (StringResponseTransformer<DockerImageInspectDetails>) json -> gson.fromJson(json, new TypeToken<DockerImageInspectDetails>() {
                }.getType()));
    }

    @Override
    public HttpStatus pushImage(final String image, AuthConfig authConfig) {
        validate(image, Strings::isEmptyOrNull, () -> "image can't be null or empty.");
        final String endpoint = String.format(IMAGE_PUSH_ENDPOINT, image);
        return httpClient.post(endpoint).toBlocking().last();
    }

    @Override
    public Observable<String> pushImageObs(final String image, AuthConfig authConfig) {
        validate(image, Strings::isEmptyOrNull, () -> "image can't be null or empty.");
        final String endpoint = String.format(IMAGE_PUSH_ENDPOINT, image);
        return httpClient.postAndReceiveResponseBuffer(endpoint, authConfig).map(buffer -> buffer.readString(Charset.defaultCharset()));
    }
}