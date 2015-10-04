package io.reactivex.docker.client;

import com.squareup.okhttp.*;
import io.reactivex.docker.client.function.JsonTransformer;
import io.reactivex.docker.client.ssl.DockerCertificates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

class OkHttpBasedRxHttpClient implements RxHttpClient {

    private final Logger logger = LoggerFactory.getLogger(OkHttpBasedRxHttpClient.class);

    private static final String DEFAULT_CERT_PATH = System.getenv("OKHTTP_DEFAULT_CERT_PATH");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private final String apiUri;

    OkHttpBasedRxHttpClient(final String host, final int port) {
        this(host, port, Optional.empty());
    }

    OkHttpBasedRxHttpClient(final String host, final int port, final Optional<String> certPath) {
        final String scheme = certPath.isPresent() ? "https" : "http";
        apiUri = scheme + "://" + host + ":" + port;
        logger.info("Base API uri {}", apiUri);
        client
                .setSslSocketFactory(
                        new DockerCertificates(
                                certPath.map(p -> Paths.get(p)).orElseGet(() -> Paths.get(DEFAULT_CERT_PATH)))
                                .sslContext().getSocketFactory());
    }

    @Override
    public <R> Observable<R> get(final String endpointPath, final JsonTransformer<R> transformer) {
        return Observable.create(subscriber -> {
            try {
                Request getRequest = new Request.Builder().url(String.format("%s/%s", apiUri, endpointPath)).header("Accept", "application/json").build();
                Call call = client.newCall(getRequest);
                Response response = call.execute();
                if (response.isSuccessful()) {
                    try (ResponseBody body = response.body()) {
                        subscriber.onNext(transformer.apply(body.string()));
                        subscriber.onCompleted();
                    }
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpointPath, e);
                subscriber.onError(new RestServiceCommunicationException(e));
            }
        });
    }

    @Override
    public Observable<String> get(final String endpointPath) {
        return get(endpointPath, JsonTransformer.identity());
    }

}
