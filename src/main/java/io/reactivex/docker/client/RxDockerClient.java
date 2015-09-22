package io.reactivex.docker.client;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
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
import static io.reactivex.docker.client.utils.Preconditions.check;
import static io.reactivex.netty.protocol.http.client.HttpClientRequest.createGet;
import static io.reactivex.netty.protocol.http.client.HttpClientRequest.createPost;

public class RxDockerClient implements MiscOperations, ContainerOperations {

    public static final String DEFAULT_DOCKER_HOST = "localhost";
    public static final int DEFAULT_DOCKER_PORT = 2375;

    private final Logger logger = LoggerFactory.getLogger(RxDockerClient.class);
    private final String apiUri;
    private final HttpClient<ByteBuf, ByteBuf> rxClient;

    public RxDockerClient(final String dockerHost, final String dockerCertPath) {
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

    /**
     * Builds the client using DOCKER_HOST and DOCKER_CERT_PATH environment variables
     *
     * @return a new instance of RxDockerClient
     */
    public static RxDockerClient fromDefaultEnv() {
        return new RxDockerClient(Optional.ofNullable(System.getenv("DOCKER_HOST")), Optional.ofNullable(System.getenv("DOCKER_CERT_PATH")));
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
    public DockerContainerResponse createContainer(DockerContainerRequest request, String name) {
        return createContainerObs(request, Optional.ofNullable(name)).toBlocking().single();
    }

    @Override
    public DockerContainerResponse createContainer(DockerContainerRequest request) {
        return createContainer(request, null);
    }

    @Override
    public Observable<DockerContainerResponse> createContainerObs(DockerContainerRequest request, Optional<String> name) {
        String content = request.toJson();
        logger.info("Creating container >>\n for json request '{}'", content);
        final String uri = name.isPresent() ? CREATE_CONTAINER_ENDPOINT + "?name=" + name.get() : CREATE_CONTAINER_ENDPOINT;
        Observable<HttpClientResponse<ByteBuf>> observable = rxClient.submit(createPost(uri).withContent(content).withHeader("Content-Type", "application/json"));
        return getObservable(uri, observable, () -> DockerContainerResponse.class);
    }

    @Override
    public ContainerInspectResponse inspectContainer(final String containerId) {
        return inspectContainerObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<ContainerInspectResponse> inspectContainerObs(final String containerId) {
        check(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_JSON_ENDPOINT, containerId);
        return getRequestObservable(uri, () -> ContainerInspectResponse.class);
    }

    @Override
    public ProcessListResponse listProcesses(final String containerId) {
        return listProcessesObs(containerId).toBlocking().single();
    }

    @Override
    public Observable<ProcessListResponse> listProcessesObs(final String containerId) {
        check(containerId, Strings::isEmptyOrNull, () -> "containerId can't be null or empty.");
        final String uri = String.format(CONTAINER_LIST_PROCESS_ENDPOINT, containerId);
        return getRequestObservable(uri, () -> ProcessListResponse.class);
    }

    private <T> Observable<T> getRequestObservable(String uri, Supplier<Type> f) {
        logger.info("Making request to uri '{}'", uri);
        Observable<HttpClientResponse<ByteBuf>> observable = rxClient.submit(createGet(uri));
        return getObservable(uri, observable, f);
    }

    private <T> Observable<T> getObservable(String uri, Observable<HttpClientResponse<ByteBuf>> observable, Supplier<Type> f) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(UPPER_CAMEL_CASE)
                .setDateFormat(DOCKER_DATE_TIME_FORMAT)
                .setPrettyPrinting()
                .create();
        return observable.
                lift(FlatResponseOperator.<ByteBuf>flatResponse()).
                doOnNext(n -> logger.info("Response for {} >>\n '{}'", uri, n.getContent().toString(Charset.defaultCharset()))).
                map(resp -> gson.fromJson(resp.getContent().toString(Charset.defaultCharset()), f.get()));
    }

    public String getApiUri() {
        return apiUri;
    }
}
