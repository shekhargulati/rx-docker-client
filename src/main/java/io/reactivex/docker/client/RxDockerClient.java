package io.reactivex.docker.client;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.reactivex.docker.client.model.DockerVersion;
import io.reactivex.docker.client.ssl.DockerCertificates;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.pipeline.ssl.DefaultFactories;
import io.reactivex.netty.protocol.http.client.FlatResponseOperator;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.client.ResponseHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;

import javax.net.ssl.SSLEngine;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Optional;

import static io.reactivex.netty.protocol.http.client.HttpClientRequest.createGet;

public class RxDockerClient {

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

        DefaultFactories.SSLContextBasedFactory sslContextBasedFactory = new DefaultFactories.SSLContextBasedFactory(new DockerCertificates(Paths.get(dockerCertPath.get())).sslContext()) {
            @Override
            public SSLEngine createSSLEngine(ByteBufAllocator allocator) {
                SSLEngine sslEngine = super.createSSLEngine(allocator);
                sslEngine.setUseClientMode(true);
                return sslEngine;
            }
        };
        rxClient = RxNetty.<ByteBuf, ByteBuf>newHttpClientBuilder(hostAndPort.getHost(), hostAndPort.getPort())
                .withSslEngineFactory(sslContextBasedFactory)
                .build();

    }

    /**
     * Builds the client using DOCKER_HOST and DOCKER_CERT_PATH environment variables
     *
     * @return a new instance of RxDockerClient
     */
    public static RxDockerClient fromDefaultEnv() {
        return new RxDockerClient(Optional.ofNullable(System.getenv("DOCKER_HOST")), Optional.ofNullable(System.getenv("DOCKER_CERT_PATH")));
    }

    public Observable<HttpClientResponse<ByteBuf>> versionObs() {
        Observable<HttpClientResponse<ByteBuf>> observable = rxClient.submit(createGet("/version"));
        return observable;
    }

    public DockerVersion getVersion() {
        Observable<HttpClientResponse<ByteBuf>> obs = versionObs();
        return obs.lift(FlatResponseOperator.<ByteBuf>flatResponse())
                .map(new Func1<ResponseHolder<ByteBuf>, ResponseHolder<DockerVersion>>() {
                    @Override
                    public ResponseHolder<DockerVersion> call(ResponseHolder<ByteBuf> holder) {
                        System.out.println(holder.getContent().toString(
                                Charset.defaultCharset()));
                        System.out.println("=======================");
                        return null;
                    }
                })
                .toBlocking().single().getContent();
    }

    public String getApiUri() {
        return apiUri;
    }
}
