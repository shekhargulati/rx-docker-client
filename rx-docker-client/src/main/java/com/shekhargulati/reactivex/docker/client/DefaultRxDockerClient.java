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

package com.shekhargulati.reactivex.docker.client;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shekhargulati.reactivex.docker.client.representations.*;
import com.shekhargulati.reactivex.docker.client.utils.Dates;
import com.shekhargulati.reactivex.docker.client.utils.StreamUtils;
import com.shekhargulati.reactivex.docker.client.utils.Strings;
import com.shekhargulati.reactivex.rxokhttp.*;
import com.shekhargulati.reactivex.rxokhttp.functions.*;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.gson.FieldNamingPolicy.UPPER_CAMEL_CASE;
import static com.shekhargulati.reactivex.docker.client.utils.StreamUtils.iteratorToStream;
import static com.shekhargulati.reactivex.docker.client.utils.Validations.validate;
import static com.shekhargulati.reactivex.rxokhttp.ClientConfig.defaultConfig;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

class DefaultRxDockerClient implements RxDockerClient {

    private static final String EMPTY_BODY = "";

    private final Logger logger = LoggerFactory.getLogger(DefaultRxDockerClient.class);
    private final String apiUri;
    private final RxHttpClient httpClient;

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(UPPER_CAMEL_CASE)
            .setDateFormat(Dates.DOCKER_DATE_TIME_FORMAT)
            .setPrettyPrinting().create();

    DefaultRxDockerClient(final String dockerHost, final String dockerCertPath) {
        this(Optional.ofNullable(dockerHost), Optional.ofNullable(dockerCertPath));
    }

    private DefaultRxDockerClient(final Optional<String> dockerHost, final Optional<String> dockerCertPath) {
        final HostAndPort hostAndPort = dockerHost.map(HostAndPort::from).orElse(HostAndPort.using(DEFAULT_DOCKER_HOST, DEFAULT_DOCKER_PORT));
        final String scheme = dockerCertPath.isPresent() ? "https" : "http";

        apiUri = scheme + "://" + hostAndPort.getHost() + ":" + hostAndPort.getPort();
        logger.info("Docker API uri {}", apiUri);
        httpClient = RxHttpClient.newRxClient(hostAndPort.getHost(), hostAndPort.getPort(), dockerCertPath, defaultConfig());
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
            if (e instanceof ServiceException) {
                logger.info("checkAuth threw RestServiceCommunicationException");
                ServiceException restException = (ServiceException) e;
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
            if (e instanceof ServiceException) {
                logger.info("checkAuth threw RestServiceCommunicationException");
                ServiceException restException = (ServiceException) e;
                return HttpStatus.of(restException.getCode(), restException.getHttpMessage());
            }
            return HttpStatus.of(500, e.getMessage());
        }).toBlocking().last();
    }

    // Container operations
    @Override
    public Observable<DockerContainer> listRunningContainerObs() {
        return listContainersObs(QueryParametersBuilder.defaultQueryParameters());
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
    public Observable<DockerContainerResponse> createContainerObs(final DockerContainerRequest request, final String name) {
        return createContainerObs(request, Optional.ofNullable(name));
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
        return httpClient.post(uri, EMPTY_BODY, ResponseTransformer.httpStatus());
    }

    @Override
    public HttpStatus stopContainer(final String containerId, final int waitInSecs) {
        return stopContainerObs(containerId, waitInSecs).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> stopContainerObs(final String containerId, final int waitInSecs) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_STOP_ENDPOINT, containerId) + "?t=" + waitInSecs;
        return httpClient.post(uri, EMPTY_BODY, ResponseTransformer.httpStatus());
    }

    @Override
    public HttpStatus restartContainer(final String containerId, final int waitInSecs) {
        return restartContainerObs(containerId, waitInSecs).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> restartContainerObs(final String containerId, final int waitInSecs) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_RESTART_ENDPOINT, containerId) + "?t=" + waitInSecs;
        return httpClient.post(uri, EMPTY_BODY, ResponseTransformer.httpStatus());
    }

    @Override
    public HttpStatus killRunningContainer(final String containerId) {
        return killRunningContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> killRunningContainerObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_KILL_ENDPOINT, containerId);
        return httpClient.post(uri, EMPTY_BODY, ResponseTransformer.httpStatus());
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
        return httpClient.post(uri, EMPTY_BODY, ResponseTransformer.httpStatus());
    }

    @Override
    public HttpStatus waitContainer(final String containerId) {
        return waitContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> waitContainerObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_WAIT_ENDPOINT, containerId);
        return httpClient.post(uri, EMPTY_BODY, ResponseTransformer.httpStatus());
    }

    @Override
    public void exportContainer(final String containerId, final Path pathToExportTo) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpointUri = String.format(CONTAINER_EXPORT_ENDPOINT, containerId);
        Observable<Buffer> bufferStream = httpClient.getResponseBufferStream(endpointUri);
        String exportFilePath = pathToExportTo.toString() + "/" + containerId + ".tar";
        writeToOutputDir(bufferStream, exportFilePath);
    }

    @Override
    public Observable<ContainerStats> containerStatsObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpointUri = String.format(CONTAINER_STATS_ENDPOINT, containerId);
        return httpClient.getResponseStream(endpointUri).map(json -> gson.fromJson(json, ContainerStats.class));
    }

    @Override
    public Observable<String> containerLogsObs(final String containerId) {
        return containerLogsObs(containerId, ContainerLogQueryParameters.withDefaultValues());
    }

    @Override
    public Observable<String> containerLogsObs(final String containerId, ContainerLogQueryParameters queryParameters) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpointUri = String.format(CONTAINER_LOGS_ENDPOINT, containerId) + queryParameters.toQueryParametersString();
        Map<String, String> headers = Stream.of(new SimpleEntry<>("Accept", "application/vnd.docker.raw-stream"))
                .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));
        return httpClient
                .getResponseStream(endpointUri, headers);
    }

    @Override
    public List<ContainerChange> inspectChangesOnContainerFilesystem(final String containerId) {
        return StreamUtils.iteratorToStream(inspectChangesOnContainerFilesystemObs(containerId).toBlocking().getIterator()).collect(toList());
    }

    @Override
    public Observable<ContainerChange> inspectChangesOnContainerFilesystemObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpoint = String.format(CONTAINER_CHANGES_ENDPOINT, containerId);
        return httpClient.get(endpoint,
                (StringResponseToCollectionTransformer<ContainerChange>) json -> {
                    logger.info("Received json >>> {}", json);
                    return gson.fromJson(json, new TypeToken<List<ContainerChange>>() {
                    }.getType());
                });
    }


    @Override
    public HttpStatus resizeContainerTty(final String containerId, QueryParameter... queryParameters) {
        return resizeContainerTtyObs(containerId, queryParameters).toBlocking().last();
    }

    @Override
    public Observable<HttpStatus> resizeContainerTtyObs(final String containerId, QueryParameter... queryParameters) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpoint = String.format(CONTAINER_RESIZE_ENDPOINT, containerId);
        return httpClient.post(endpoint, queryParameters);
    }

    @Override
    public HttpStatus pauseContainer(final String containerId) {
        return pauseContainerObs(containerId).toBlocking().last();
    }

    @Override
    public Observable<HttpStatus> pauseContainerObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpoint = String.format(CONTAINER_PAUSE_ENDPOINT, containerId);
        return httpClient.post(endpoint);
    }

    @Override
    public HttpStatus unpauseContainer(final String containerId) {
        return unpauseContainerObs(containerId).toBlocking().last();
    }

    @Override
    public Observable<HttpStatus> unpauseContainerObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpoint = String.format(CONTAINER_UNPAUSE_ENDPOINT, containerId);
        return httpClient.post(endpoint);
    }

    @Override
    public Observable<String> attachContainerObs(final String containerId, QueryParameter... queryParameters) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpoint = String.format(CONTAINER_ATTACH_ENDPOINT, containerId);
        return httpClient.postAndReceiveResponse(endpoint, queryParameters);
    }

    @Override
    public ContainerArchiveInformation containerArchiveInformation(final String containerId, final String path) {
        Response response = containerArchiveInformationObs(containerId, path).toBlocking().last();
        String containerInfo = response.header("X-Docker-Container-Path-Stat");

        final String containerInfoJson = new String(Base64.getDecoder().decode(containerInfo), Charset.defaultCharset());
        return gson.fromJson(containerInfoJson, ContainerArchiveInformation.class);
    }

    @Override
    public Observable<Response> containerArchiveInformationObs(final String containerId, final String path) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpoint = String.format(CONTAINER_ARCHIVE_ENDPOINT, containerId);
        return httpClient.head(endpoint, QueryParameter.of("path", path));
    }

    @Override
    public void containerArchive(final String containerId, final String path, Path pathToExportTo) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String endpointUri = String.format(CONTAINER_ARCHIVE_ENDPOINT, containerId);
        Observable<Buffer> bufferStream = httpClient.getResponseBufferStream(endpointUri, QueryParameter.of("path", path));
        String exportFilePath = pathToExportTo.toString() + "/" + containerId + ".tar";
        writeToOutputDir(bufferStream, exportFilePath);
    }

    @Override
    public Observable<ExecCreateResponse> execCreateObs(final String containerId, ExecCreateRequest request) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        validate(request.getCmd(), c -> c == null || c.size() == 0, () -> "cmd can't be empty");
        final String endpointUri = String.format(CONTAINER_EXEC_CREATE_ENDPOINT, containerId);
        String jsonBody = gson.toJson(request);
        return httpClient.post(endpointUri, jsonBody, (ResponseBodyTransformer<ExecCreateResponse>) (responseBody) -> {
            String json = responseBody.string();
            return gson.fromJson(json, ExecCreateResponse.class);
        });
    }

    @Override
    public Observable<ExecCreateResponse> execCreateObs(final String containerId, final String... cmd) {
        return execCreateObs(containerId, ExecCreateRequest.withCmd(Arrays.asList(cmd)));
    }

    @Override
    public Observable<String> execStartObs(final String execId, ExecStartRequest request) {
        validate(execId, Strings::isEmptyOrNull, () -> "execId can't be null or empty.");
        final String endpointUri = String.format(CONTAINER_EXEC_START_ENDPOINT, execId);
        String jsonBody = gson.toJson(request);
        return httpClient.postAndReceiveStream(endpointUri, jsonBody);
    }

    @Override
    public Observable<String> execStartObs(final String execId) {
        return execStartObs(execId, ExecStartRequest.withDefaults());
    }

    // Image Endpoint
    @Override
    public HttpStatus pullImage(final String fromImage) {
        return pullImage(fromImage, (String) null);
    }

    @Override
    public HttpStatus pullImage(final String fromImage, AuthConfig authConfig) {
        return pullImage(fromImage, null, authConfig);
    }

    @Override
    public HttpStatus pullImage(final String fromImage, final String tag) {
        return pullImage(fromImage, null, tag);
    }

    @Override
    public HttpStatus pullImage(final String fromImage, final String tag, AuthConfig authConfig) {
        return pullImage(fromImage, null, tag, authConfig);
    }

    @Override
    public HttpStatus pullImage(final String fromImage, final String user, final String tag) {
        return pullImage(fromImage, user, tag, null);
    }

    @Override
    public HttpStatus pullImage(final String fromImage, final String user, final String tag, AuthConfig authConfig) {
        return pullImageInternal(fromImage, user, tag, authConfig);
    }

    private HttpStatus pullImageInternal(final String fromImage, final String user, final String tag, AuthConfig authConfig) {
        Observable<String> imageObs = pullImageObs(fromImage, user, tag, authConfig);
        HttpStatusSubscriber subscriber = new HttpStatusSubscriber();
        imageObs.subscribe(subscriber);
        subscriber.unsubscribe();
        return subscriber.getStatus();
    }

    @Override
    public Observable<String> pullImageObs(final String fromImage) {
        return pullImageObs(fromImage, null, null, null);
    }

    @Override
    public Observable<String> pullImageObs(final String fromImage, final String user, final String tag) {
        return pullImageObs(fromImage, user, tag, null);
    }

    @Override
    public Observable<String> pullImageObs(final String fromImage, final String user, final String tag, AuthConfig authConfig) {
        return pullImageObsInternal(fromImage, Optional.ofNullable(user), Optional.ofNullable(tag), Optional.ofNullable(authConfig));
    }

    private Observable<String> pullImageObsInternal(final String fromImage, final Optional<String> user, final Optional<String> tag, Optional<AuthConfig> authConfig) {
        validate(fromImage, Strings::isEmptyOrNull, () -> "fromImage can't be null or empty.");
        final String endpoint = String.format(IMAGE_CREATE_ENDPOINT, user.map(u -> u + "/").orElse(""), fromImage, tag.orElse("latest"));
        Map<String, String> headers = new HashMap<>();
        if (authConfig.isPresent()) {
            headers.put("X-Registry-Auth", authConfig.get().xAuthHeader());
        }
        return httpClient.postAndReceiveResponse(endpoint, headers);
    }

    @Override
    public Stream<DockerImage> listAllImages() {
        return listImages(ImageListQueryParameters.allImagesQueryParameters());
    }

    @Override
    public Stream<DockerImage> listImages(final String imageName) {
        return listImages(ImageListQueryParameters.queryParameterWithImageName(imageName));
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
        return httpClient.postTarStream(endpoint, pathToTarArchive, (BufferTransformer<String>) buf -> buf.readString(Charset.defaultCharset()));
    }

    @Override
    public Observable<String> buildImageObs(final String repositoryName, BuildImageQueryParameters queryParameters) {
        final String endpoint = String.format("%s?t=%s", IMAGE_BUILD_ENDPOINT, repositoryName) + queryParameters.toQueryParameterString();
        return httpClient.postAndReceiveResponse(endpoint);
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
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Registry-Auth", authConfig.xAuthHeader());
        return httpClient.post(endpoint, headers).toBlocking().last();
    }

    @Override
    public Observable<String> pushImageObs(final String image, AuthConfig authConfig) {
        validate(image, Strings::isEmptyOrNull, () -> "image can't be null or empty.");
        final String endpoint = String.format(IMAGE_PUSH_ENDPOINT, image);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Registry-Auth", authConfig.xAuthHeader());
        return httpClient
                .postAndReceiveResponse(endpoint, headers, r -> r.contains("errorDetail"));
    }

    @Override
    public Path getTarballForAllImagesInRepository(final String image, Path exportDir) {
        validate(image, Strings::isEmptyOrNull, () -> "image can't be null or empty.");
        validate(exportDir, p -> !p.toFile().exists(), () -> "exportDir should exists.");
        final String endpoint = String.format(IMAGE_GET_ARCHIVE_TARBALL_FOR_REPOSITORY, image);
        Observable<Buffer> bufferStream = httpClient.getResponseBufferStream(endpoint);
        Path exportFilePath = exportDir.resolve(image + ".tar");
        writeToOutputDir(bufferStream, exportFilePath);
        return exportFilePath;
    }

    @Override
    public Path getTarballContainingAllImages(Path exportDir, String filename, ImageTag... imageTags) {
        validate(filename, Strings::isEmptyOrNull, () -> "filename can't be null or empty.");
        validate(exportDir, p -> !p.toFile().exists(), () -> "exportDir should exists.");
        QueryParameter[] queryParameters = Arrays.stream(imageTags).map(i -> QueryParameter.of("names", String.format("%s%s", i.getImage(), i.getTag().map(t -> String.format(":%s", t)).orElse("")))).toArray(QueryParameter[]::new);
        Observable<Buffer> bufferStream = httpClient.getResponseBufferStream(IMAGE_GET_ARCHIVE_TARBALL, queryParameters);
        Path exportFilePath = exportDir.resolve(filename + ".tar");
        writeToOutputDir(bufferStream, exportFilePath.toAbsolutePath());
        return exportFilePath;
    }

    @Override
    public HttpStatus loadImagesAndTagsTarball(final Path pathToTarArchive) {
        return loadImagesAndTagsTarballObs(pathToTarArchive).toBlocking().last();
    }

    @Override
    public Observable<HttpStatus> loadImagesAndTagsTarballObs(final Path pathToTarArchive) {
        validate(pathToTarArchive, path -> path == null, () -> "path to archive can't be null");
        validate(pathToTarArchive, path -> !path.toFile().exists(), () -> String.format("%s can't be resolved to a tar file", pathToTarArchive.toAbsolutePath().toString()));
        final String endpoint = IMAGE_LOAD;
        return httpClient.postTarStream(endpoint, pathToTarArchive);
    }

    @Override
    public HttpStatus createImage(final String name, final Path imageToLoad) {
        String response = createImageObs(name, imageToLoad).toBlocking().last();
        Map<String, String> o = gson.fromJson(response, new TypeToken<Map<String, String>>() {
        }.getType());
        tagImage(o.get("status"), ImageTagQueryParameters.with(name, "latest"));
        return HttpStatus.OK;
    }

    @Override
    public Observable<String> createImageObs(final String name, final Path imageToLoad) {
        validate(imageToLoad, path -> path == null, () -> "imageToLoad path can't be null");
        validate(imageToLoad, path -> !path.toFile().exists(), () -> String.format("%s can't be resolved to a tar file", imageToLoad.toAbsolutePath().toString()));
        final String endpoint = String.format(IMAGE_CREATE_ENDPOINT_FROM_SRC, "-", name);
        return httpClient.postTarStream(endpoint, imageToLoad, ResponseTransformer.fromBody(responseBody -> responseBody.string()));
    }


    private void writeToOutputDir(Observable<Buffer> bufferStream, final Path exportFilePath) {
        writeToOutputDir(bufferStream, exportFilePath.toAbsolutePath().toString());
    }

    private void writeToOutputDir(Observable<Buffer> bufferStream, final String exportFilePath) {
        try (FileOutputStream out = new FileOutputStream(exportFilePath)) {
            Subscriber<Buffer> httpSubscriber = new Subscriber<Buffer>() {
                @Override
                public void onCompleted() {
                    logger.info("Exported to path {}", exportFilePath);
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


}

