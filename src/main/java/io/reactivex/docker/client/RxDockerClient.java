package io.reactivex.docker.client;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.ResponseBody;
import io.reactivex.docker.client.representations.*;
import io.reactivex.docker.client.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.List;
import java.util.Optional;

import static com.google.gson.FieldNamingPolicy.UPPER_CAMEL_CASE;
import static io.reactivex.docker.client.QueryParametersBuilder.defaultQueryParameters;
import static io.reactivex.docker.client.function.ResponseTransformer.httpStatus;
import static io.reactivex.docker.client.utils.Dates.DOCKER_DATE_TIME_FORMAT;
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
                        json -> gson.fromJson(json, DockerVersion.class));
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
                        json -> gson.fromJson(json, DockerInfo.class));
    }

    @Override
    public DockerInfo info() {
        return infoObs().
                toBlocking().
                single();
    }

    // Container operations
    @Override
    public Observable<List<DockerContainer>> listRunningContainerObs() {
        return listContainersObs(defaultQueryParameters());
    }

    @Override
    public List<DockerContainer> listRunningContainers() {
        return listRunningContainerObs().flatMap(Observable::from).toList().toBlocking().single();
    }

    @Override
    public Observable<List<DockerContainer>> listAllContainersObs() {
        return listContainersObs(new QueryParametersBuilder().withAll(true).createQueryParameters());
    }

    @Override
    public List<DockerContainer> listAllContainers() {
        return listAllContainersObs().flatMap(Observable::from).toList().toBlocking().single();
    }

    @Override
    public List<DockerContainer> listContainers(QueryParameters queryParameters) {
        return listContainersObs(queryParameters).flatMap(Observable::from).toList().toBlocking().single();
    }

    @Override
    public Observable<List<DockerContainer>> listContainersObs(QueryParameters queryParameters) {
        final String query = queryParameters.toQuery();
        String url = String.format(CONTAINER_ENDPOINT, query);
        return httpClient.get(url,
                json -> gson.fromJson(json, new TypeToken<List<DockerContainer>>() {
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
        logger.info("Creating container >>\n for json request '{}'", content);
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
                        json -> gson.fromJson(json, ContainerInspectResponse.class));
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
                        json -> gson.fromJson(json, ProcessListResponse.class));
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
        final String uri = String.format(CONTAINER_STOP_ENDPOINT, containerId) + "?t=" + waitInSecs;
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public HttpStatus restartContainer(final String containerId, final int waitInSecs) {
        return restartContainerObs(containerId, waitInSecs).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> restartContainerObs(final String containerId, final int waitInSecs) {
        final String uri = String.format(CONTAINER_RESTART_ENDPOINT, containerId) + "?t=" + waitInSecs;
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public HttpStatus killRunningContainer(final String containerId) {
        return killRunningContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> killRunningContainerObs(final String containerId) {
        final String uri = String.format(CONTAINER_KILL_ENDPOINT, containerId);
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public HttpStatus removeContainer(final String containerId) {
        return removeContainer(containerId, false, false);
    }

    @Override
    public HttpStatus removeContainer(final String containerId, final boolean removeVolume, final boolean force) {
        return removeContainerObs(containerId, removeVolume, force).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> removeContainerObs(final String containerId) {
        return removeContainerObs(containerId, false, false);
    }

    @Override
    public Observable<HttpStatus> removeContainerObs(final String containerId, final boolean removeVolume, final boolean force) {
        final String uri = String.format(CONTAINER_REMOVE_ENDPOINT, containerId) + "?v=" + removeVolume + "&force=" + force;
        return httpClient.delete(uri);
    }

    @Override
    public HttpStatus renameContainer(final String containerId, final String newName) {
        return renameContainerObs(containerId, newName).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> renameContainerObs(final String containerId, final String newName) {
        final String uri = String.format(CONTAINER_RENAME_ENDPOINT, containerId) + "?name=" + newName;
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public HttpStatus waitContainer(final String containerId) {
        return waitContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpStatus> waitContainerObs(final String containerId) {
        final String uri = String.format(CONTAINER_WAIT_ENDPOINT, containerId);
        return httpClient.post(uri, EMPTY_BODY, httpStatus());
    }

    @Override
    public void exportContainer(final String containerId, final String filepath) {

    }

    @Override
    public ContainerStats containerStats(final String containerId) {
        return containerStatsObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<ContainerStats> containerStatsObs(final String containerId) {
        return null;
    }

    @Override
    public HttpStatus pullImage(final String fromImage) {
        return null;
    }

    @Override
    public Observable<HttpStatus> pullImageObs(final String fromImage) {
        return null;
    }


    private String toRestEndpoint(String endpoint, String containerId, String... queryParameters) {
        String baseUrl = String.format(endpoint, containerId);
        if (queryParameters == null || queryParameters.length == 0) {
            logger.info("Making request to {}", baseUrl);
            return baseUrl;
        }
        String uri = String.format("%s?%s", baseUrl, String.join("&", queryParameters));
        logger.info("Making request to {}", uri);
        return uri;
    }

}


