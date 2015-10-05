package io.reactivex.docker.client;

import com.squareup.okhttp.*;
import io.reactivex.docker.client.function.JsonTransformer;
import io.reactivex.docker.client.function.ResponseBodyTransformer;
import io.reactivex.docker.client.function.ResponseTransformer;
import io.reactivex.docker.client.ssl.DockerCertificates;
import okio.Buffer;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static io.reactivex.docker.client.function.ResponseTransformer.httpStatus;

class OkHttpBasedRxHttpClient implements RxHttpClient {

    private final Logger logger = LoggerFactory.getLogger(OkHttpBasedRxHttpClient.class);

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final String apiUri;

    OkHttpBasedRxHttpClient(final String host, final int port) {
        this(host, port, Optional.empty());
    }

    OkHttpBasedRxHttpClient(final String host, final int port, final Optional<String> certPath) {
        final String scheme = certPath.isPresent() ? "https" : "http";
        apiUri = scheme + "://" + host + ":" + port;
        logger.info("Base API uri {}", apiUri);
        if (certPath.isPresent()) {
            client.setSslSocketFactory(new DockerCertificates(Paths.get(certPath.get())).sslContext().getSocketFactory());
        }
    }

    @Override
    public <R> Observable<R> get(final String endpoint, final JsonTransformer<R> transformer) {
        return Observable.create(subscriber -> {
            try {
                final String url = String.format("%s/%s", apiUri, endpoint);
                Request getRequest = new Request.Builder().url(url).build();
                logger.info("Making GET request to {}", url);
                Call call = client.newCall(getRequest);
                Response response = call.execute();
                logger.info("Received response >> {} with headers >> {}", response.code(), response.headers());
                if (response.isSuccessful()) {
                    try (ResponseBody body = response.body()) {
                        subscriber.onNext(transformer.apply(body.string()));
                        subscriber.onCompleted();
                    }
                } else {
                    subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message())));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new RestServiceCommunicationException(e));
            }
        });
    }

    @Override
    public Observable<Buffer> getBuffer(final String endpoint) {
        return Observable.create(subscriber -> {
            try {
                final String url = String.format("%s/%s", apiUri, endpoint);
                Request getRequest = new Request.Builder().url(url).build();
                logger.info("Making GET request to {}", url);
                Call call = client.newCall(getRequest);
                Response response = call.execute();
                logger.info("Received response >> {} with headers >> {}", response.code(), response.headers());
                if (response.isSuccessful()) {
                    try (ResponseBody body = response.body()) {
                        BufferedSource source = body.source();
                        while (!source.exhausted()) {
                            subscriber.onNext(source.buffer());
                        }
                        subscriber.onCompleted();
                    }
                } else {
                    subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message())));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new RestServiceCommunicationException(e));
            }
        });
    }

    @Override
    public Observable<String> get(final String endpointPath) {
        return get(endpointPath, JsonTransformer.identity());
    }

    @Override
    public <R> Observable<R> post(final String endpoint, final String postBody, final ResponseTransformer<R> transformer) {
        return Observable.create(subscriber -> {
            try {
                RequestBody requestBody = RequestBody.create(JSON, postBody);
                final String url = String.format("%s/%s", apiUri, endpoint);
                Request getRequest = new Request.Builder()
                        .header("Content-Type", "application/json")
                        .url(url)
                        .post(requestBody)
                        .build();
                logger.info("Making POST request to {}", url);
                Call call = client.newCall(getRequest);
                Response response = call.execute();
                if (response.isSuccessful()) {
                    subscriber.onNext(transformer.apply(response));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message())));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new RestServiceCommunicationException(e));
            }
        });
    }

    @Override
    public Observable<HttpStatus> post(final String endpoint) {
        return post(endpoint, EMPTY_BODY, httpStatus());
    }

    @Override
    public <R> Observable<R> post(final String endpoint, final ResponseBodyTransformer<R> bodyTransformer) {
        return post(endpoint, EMPTY_BODY, ResponseTransformer.fromBody(bodyTransformer));
    }

    @Override
    public <R> Observable<R> post(final String endpoint, final String postBody, final ResponseBodyTransformer<R> bodyTransformer) {
        return post(endpoint, postBody, ResponseTransformer.fromBody(bodyTransformer));
    }

    @Override
    public Observable<HttpStatus> delete(String endpoint) {
        return Observable.create(subscriber -> {
            try {
                final String url = String.format("%s/%s", apiUri, endpoint);
                Request deleteRequest = new Request.Builder()
                        .header("Content-Type", "application/json")
                        .url(url)
                        .delete()
                        .build();
                logger.info("Making DELETE request to {}", url);
                Call call = client.newCall(deleteRequest);
                Response response = call.execute();
                if (response.isSuccessful()) {
                    subscriber.onNext(new HttpStatus(response.code(), response.message()));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message())));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new RestServiceCommunicationException(e));
            }
        });
    }

}
