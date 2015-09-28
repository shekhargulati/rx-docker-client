package io.reactivex.docker.client;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.docker.client.representations.*;
import io.reactivex.docker.client.ssl.DockerCertificates;
import io.reactivex.docker.client.utils.Strings;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.pipeline.ssl.DefaultFactories;
import io.reactivex.netty.protocol.http.client.FlatResponseOperator;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientBuilder;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.net.ssl.SSLEngine;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.gson.FieldNamingPolicy.UPPER_CAMEL_CASE;
import static io.reactivex.docker.client.QueryParametersBuilder.defaultQueryParameters;
import static io.reactivex.docker.client.utils.Dates.DOCKER_DATE_TIME_FORMAT;
import static io.reactivex.docker.client.utils.Validations.validate;
import static io.reactivex.netty.protocol.http.client.HttpClientRequest.createGet;
import static io.reactivex.netty.protocol.http.client.HttpClientRequest.createPost;

class RxDockerClient implements DockerClient {

    private static final String EMPTY_BODY = "";

    private final Logger logger = LoggerFactory.getLogger(RxDockerClient.class);
    private final String apiUri;
    private final HttpClient<ByteBuf, ByteBuf> rxClient;

    RxDockerClient(final String dockerHost, final String dockerCertPath) {
        this(Optional.ofNullable(dockerHost), Optional.ofNullable(dockerCertPath));
    }

    private RxDockerClient(final Optional<String> dockerHost, final Optional<String> dockerCertPath) {
        final HostAndPort hostAndPort = dockerHost.map(endpoint -> HostAndPort.from(endpoint)).orElse(HostAndPort.using(DEFAULT_DOCKER_HOST, DEFAULT_DOCKER_PORT));
        final String scheme = dockerCertPath.isPresent() ? "https" : "http";

        apiUri = new StringBuilder(scheme).append("://").append(hostAndPort.getHost()).append(":").append(hostAndPort.getPort()).toString();
        logger.info("Docker API uri {}", apiUri);

        HttpClientBuilder<ByteBuf, ByteBuf> builder = RxNetty.<ByteBuf, ByteBuf>newHttpClientBuilder(hostAndPort.getHost(), hostAndPort.getPort());

        if (dockerCertPath.isPresent()) {
            DefaultFactories.SSLContextBasedFactory sslContextBasedFactory = new DefaultFactories.SSLContextBasedFactory(new DockerCertificates(Paths.get(dockerCertPath.get())).sslContext()) {
                @Override
                public SSLEngine createSSLEngine(ByteBufAllocator allocator) {
                    SSLEngine sslEngine = super.createSSLEngine(allocator);
                    sslEngine.setUseClientMode(true);
                    return sslEngine;
                }
            };
            builder.withSslEngineFactory(sslContextBasedFactory);
        }
        rxClient = builder.build();
    }

    @Override
    public String getApiUri() {
        return apiUri;
    }

    // Misc operations
    @Override
    public Observable<DockerVersion> serverVersionObs() {
        return getRequestObservable(VERSION_ENDPOINT, () -> DockerVersion.class);
    }

    @Override
    public DockerVersion serverVersion() {
        return serverVersionObs().
                toBlocking().
                single();
    }

    @Override
    public Observable<DockerInfo> infoObs() {
        return getRequestObservable(INFO_ENDPOINT, () -> DockerInfo.class);
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
        return listRunningContainerObs().flatMap((List<DockerContainer> a) -> Observable.from(a)).toList().toBlocking().single();
    }

    @Override
    public Observable<List<DockerContainer>> listAllContainersObs() {
        return listContainersObs(new QueryParametersBuilder().withAll(true).createQueryParameters());
    }

    @Override
    public List<DockerContainer> listAllContainers() {
        return listAllContainersObs().flatMap((List<DockerContainer> containers) -> Observable.from(containers)).toList().toBlocking().single();
    }

    @Override
    public List<DockerContainer> listContainers(QueryParameters queryParameters) {
        return listContainersObs(queryParameters).flatMap((List<DockerContainer> containers) -> Observable.from(containers)).toList().toBlocking().single();
    }

    @Override
    public Observable<List<DockerContainer>> listContainersObs(QueryParameters queryParameters) {
        final String query = queryParameters.toQuery();
        String url = String.format(CONTAINER_ENDPOINT, query);
        return getRequestObservable(
                url, () ->
                        new TypeToken<List<DockerContainer>>() {
                        }.getType()
        );
    }


    @Override
    public DockerContainerResponse createContainer(final DockerContainerRequest request, final String name) {
        return createContainerObs(request, Optional.ofNullable(name)).toBlocking().single();
    }

    @Override
    public DockerContainerResponse createContainer(final DockerContainerRequest request) {
        return createContainer(request, null);
    }

    @Override
    public Observable<DockerContainerResponse> createContainerObs(final DockerContainerRequest request, final Optional<String> name) {
        String content = request.toJson();
        logger.info("Creating container >>\n for json request '{}'", content);
        final String uri = name.isPresent() ? CREATE_CONTAINER_ENDPOINT + "?name=" + name.get() : CREATE_CONTAINER_ENDPOINT;
        Observable<HttpClientResponse<ByteBuf>> observable = postRequestObservable(uri, content);
        return observableResponse(uri, observable, () -> DockerContainerResponse.class);
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
        return containerLifecycle(containerId, CONTAINER_START_ENDPOINT);
    }

    @Override
    public HttpResponseStatus stopContainer(final String containerId, final int waitInSecs) {
        return stopContainerObs(containerId, waitInSecs).toBlocking().single();
    }

    @Override
    public Observable<HttpResponseStatus> stopContainerObs(final String containerId, final int waitInSecs) {
        return containerLifecycle(containerId, CONTAINER_STOP_ENDPOINT);
    }

    @Override
    public HttpResponseStatus restartContainer(final String containerId, final int waitInSecs) {
        return restartContainerObs(containerId, waitInSecs).toBlocking().single();
    }

    @Override
    public Observable<HttpResponseStatus> restartContainerObs(final String containerId, final int waitInSecs) {
        return containerLifecycle(containerId, CONTAINER_RESTART_ENDPOINT);
    }

    // internal methods
    private Observable<HttpClientResponse<ByteBuf>> postRequestObservable(String uri, String content) {
        return rxClient.submit(createPost(uri).withContent(content).withHeader("Content-Type", "application/json"));
    }

    private <T> Observable<T> getRequestObservable(String uri, Supplier<Type> f) {
        logger.info("Making request to uri '{}'", uri);
        Observable<HttpClientResponse<ByteBuf>> observable = rxClient.submit(createGet(uri));
        return observableResponse(uri, observable, f);
    }

    private Observable<HttpResponseStatus> observableHeaderResponse(Observable<HttpClientResponse<ByteBuf>> observable) {
        return observable.
                lift(FlatResponseOperator.<ByteBuf>flatResponse()).
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

    private Observable<HttpResponseStatus> containerLifecycle(final String containerId, final String endpoint) {
        validate(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(endpoint, containerId);
        Observable<HttpClientResponse<ByteBuf>> responseObservable = postRequestObservable(uri, EMPTY_BODY);
        return observableHeaderResponse(responseObservable);
    }

}
