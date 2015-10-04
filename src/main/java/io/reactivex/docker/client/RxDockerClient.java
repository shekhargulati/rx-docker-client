package io.reactivex.docker.client;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.ResponseBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.docker.client.function.ContainerEndpointUriFunction;
import io.reactivex.docker.client.function.HttpExecutionFunction;
import io.reactivex.docker.client.function.TriFunction;
import io.reactivex.docker.client.representations.*;
import io.reactivex.docker.client.utils.Strings;
import io.reactivex.netty.protocol.http.client.FlatResponseOperator;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.gson.FieldNamingPolicy.UPPER_CAMEL_CASE;
import static io.reactivex.docker.client.QueryParametersBuilder.defaultQueryParameters;
import static io.reactivex.docker.client.utils.Dates.DOCKER_DATE_TIME_FORMAT;
import static io.reactivex.docker.client.utils.Validations.validate;
import static io.reactivex.netty.protocol.http.client.HttpClientRequest.*;

class RxDockerClient implements DockerClient {

    private static final String EMPTY_BODY = "";

    private final Logger logger = LoggerFactory.getLogger(RxDockerClient.class);
    private final String apiUri;
    private final HttpClient<ByteBuf, ByteBuf> rxClient = null;
    private final RxHttpClient httpClient;

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
                        json -> new Gson().fromJson(json, DockerVersion.class));
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
                        json -> new Gson().fromJson(json, DockerInfo.class));
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
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(UPPER_CAMEL_CASE)
                .setDateFormat(DOCKER_DATE_TIME_FORMAT)
                .setPrettyPrinting()
                .create();
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
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(UPPER_CAMEL_CASE)
                .setDateFormat(DOCKER_DATE_TIME_FORMAT)
                .setPrettyPrinting()
                .create();
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
        return getRequestObservable(uri, () -> ContainerInspectResponse.class);
    }

    @Override
    public ProcessListResponse listProcesses(final String containerId) {
        return listProcessesObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<ProcessListResponse> listProcessesObs(final String containerId) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_LIST_PROCESS_ENDPOINT, containerId);
        return getRequestObservable(uri, () -> ProcessListResponse.class);
    }

    @Override
    public HttpResponseStatus startContainer(final String containerId) {
        return startContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpResponseStatus> startContainerObs(final String containerId) {
        return containerPostAction(containerId, CONTAINER_START_ENDPOINT);
    }

    @Override
    public HttpResponseStatus stopContainer(final String containerId, final int waitInSecs) {
        return stopContainerObs(containerId, waitInSecs).toBlocking().single();
    }

    @Override
    public Observable<HttpResponseStatus> stopContainerObs(final String containerId, final int waitInSecs) {
        return containerPostAction(containerId, CONTAINER_STOP_ENDPOINT, "t=" + waitInSecs);
    }

    @Override
    public HttpResponseStatus restartContainer(final String containerId, final int waitInSecs) {
        return restartContainerObs(containerId, waitInSecs).toBlocking().single();
    }

    @Override
    public Observable<HttpResponseStatus> restartContainerObs(final String containerId, final int waitInSecs) {
        return containerPostAction(containerId, CONTAINER_RESTART_ENDPOINT, "t=" + waitInSecs);
    }

    @Override
    public HttpResponseStatus killRunningContainer(final String containerId) {
        return killRunningContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpResponseStatus> killRunningContainerObs(final String containerId) {
        return containerPostAction(containerId, CONTAINER_KILL_ENDPOINT);
    }

    @Override
    public HttpResponseStatus removeContainer(final String containerId) {
        return removeContainer(containerId, false, false);
    }

    @Override
    public HttpResponseStatus removeContainer(final String containerId, final boolean removeVolume, final boolean force) {
        return removeContainerObs(containerId, removeVolume, force).toBlocking().single();
    }

    @Override
    public Observable<HttpResponseStatus> removeContainerObs(final String containerId) {
        return removeContainerObs(containerId, false, false);
    }

    @Override
    public Observable<HttpResponseStatus> removeContainerObs(final String containerId, final boolean removeVolume, final boolean force) {
        return containerRemoveAction(containerId, CONTAINER_REMOVE_ENDPOINT, "v=" + removeVolume, "force=" + force);
    }

    @Override
    public HttpResponseStatus renameContainer(final String containerId, final String newName) {
        return renameContainerObs(containerId, newName).toBlocking().single();
    }

    @Override
    public Observable<HttpResponseStatus> renameContainerObs(final String containerId, final String newName) {
        return containerPostAction(containerId, CONTAINER_RENAME_ENDPOINT, "name=" + newName);
    }

    @Override
    public HttpResponseStatus waitContainer(final String containerId) {
        return waitContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<HttpResponseStatus> waitContainerObs(final String containerId) {
        return containerPostAction(containerId, CONTAINER_WAIT_ENDPOINT);
    }

    @Override
    public void exportContainer(final String containerId, final String filepath) {
        try (FileOutputStream out = new FileOutputStream(filepath)) {
            String uri = "/containers/" + containerId + "/export";
            rxClient.submit(createGet(uri).withHeader("Accept", "application/octet-stream"))
                    .flatMap((HttpClientResponse<ByteBuf> resp) -> resp.getContent().map(ByteBufInputStream::new))
                    .toBlocking()
                    .forEach(str -> {
                        try {
                            logger.info("Processing...");
                            final byte[] buffer = new byte[1024];
                            int n = 0;
                            while (-1 != (n = str.read(buffer))) {
                                out.write(buffer, 0, n);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ContainerStats containerStats(final String containerId) {
        return containerStatsObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<ContainerStats> containerStatsObs(final String containerId) {
        String uri = "/containers/" + containerId + "/stats";
        return getRequestObservable(uri, () -> ContainerStats.class);
    }

    @Override
    public HttpResponseStatus pullImage(final String fromImage) {
        return null;
    }

    @Override
    public Observable<HttpClientResponse<ByteBuf>> pullImageObs(final String fromImage) {
        String uri = "/images/create?fromImage=" + fromImage;
        Observable<HttpClientResponse<ByteBuf>> observable = rxClient.submit(createPost(uri).withContent(EMPTY_BODY)
                .withHeader("Content-Type", "application/json"));
        return observable;
    }


    // internal methods

    private <T> Observable<T> getRequestObservable(String uri, Supplier<Type> f) {
        logger.info("Making request to uri '{}'", uri);
        Observable<HttpClientResponse<ByteBuf>> observable = rxClient.submit(createGet(uri));
        return observableResponse(uri, observable, f);
    }

    private Observable<HttpResponseStatus> observableHeaderResponse(Observable<HttpClientResponse<ByteBuf>> observable) {
        return observable.
                lift(FlatResponseOperator.<ByteBuf>flatResponse()).
                doOnNext(msg -> logger.info("Received msg {}", msg.getContent().toString(Charset.defaultCharset()))).
                doOnError(error -> logger.error("Exception >>> ", error)).
                map(resp -> resp.getResponse().getStatus());
    }

    private <T> Observable<T> observableResponse(String uri, Observable<HttpClientResponse<ByteBuf>> observable, Supplier<Type> supplier) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(UPPER_CAMEL_CASE)
                .setDateFormat(DOCKER_DATE_TIME_FORMAT)
                .setPrettyPrinting()
                .create();
        return observable.
                lift(FlatResponseOperator.<ByteBuf>flatResponse()).
                doOnNext(n -> logger.info("Response for {} >>\n '{}'", uri, n.getContent().toString(Charset.defaultCharset()))).
                doOnError(error -> logger.error("Exception >>> ", error)).
                map(resp -> gson.fromJson(resp.getContent().toString(Charset.defaultCharset()), supplier.get()));
    }

    private Observable<HttpResponseStatus> containerPostAction(final String containerId, final String endpoint, final String... queryParameters) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        ContainerEndpointUriFunction cFx = this::toRestEndpoint;
        TriFunction<String, String, String[], Observable<HttpClientResponse<ByteBuf>>> fx = cFx.andThen(EMPTY_BODY, httpPostExecutionFunction());
        return containerAction(HttpMethod.POST, containerId, endpoint, queryParameters, fx);
    }

    private Observable<HttpResponseStatus> containerRemoveAction(final String containerId, final String endpoint, final String... queryParameters) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        ContainerEndpointUriFunction cFx = this::toRestEndpoint;
        TriFunction<String, String, String[], Observable<HttpClientResponse<ByteBuf>>> fx = cFx.andThen(EMPTY_BODY, httpDeleteExecutionFunction());
        return containerAction(HttpMethod.DELETE, containerId, endpoint, queryParameters, fx);
    }

    private Observable<HttpResponseStatus> containerAction(final HttpMethod httpMethod, final String containerId, final String endpoint, final String[] queryParameters, final TriFunction<String, String, String[], Observable<HttpClientResponse<ByteBuf>>> fx) {
        logger.info("Making {} request", httpMethod.name());
        return observableHeaderResponse(fx.apply(endpoint, containerId, queryParameters));
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


    private HttpExecutionFunction httpPostExecutionFunction() {
        return (uri, content) -> rxClient.submit(createPost(uri).withContent(content).withHeader("Content-Type", "application/json"));
    }

    private HttpExecutionFunction httpDeleteExecutionFunction() {
        return (uri, content) -> rxClient.submit(createDelete(uri).withHeader("Content-Type", "application/json"));
    }

}


