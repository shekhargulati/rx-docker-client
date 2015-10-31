package io.reactivex.docker.client.http_client;

import com.squareup.okhttp.*;
import io.reactivex.docker.client.AuthConfig;
import io.reactivex.docker.client.function.*;
import io.reactivex.docker.client.ssl.DockerCertificates;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static io.reactivex.docker.client.function.ResponseTransformer.httpStatus;

class OkHttpBasedRxHttpClient implements RxHttpClient {

    private final Logger logger = LoggerFactory.getLogger(OkHttpBasedRxHttpClient.class);

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType OCTET = MediaType.parse("application/octet-stream; charset=utf-8");
    public static final MediaType TAR = MediaType.parse("application/tar; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final String baseApiUrl;

    public OkHttpBasedRxHttpClient(String baseApiUrl) {
        this.baseApiUrl = baseApiUrl;
    }

    OkHttpBasedRxHttpClient(final String host, final int port) {
        this(host, port, Optional.empty());
    }

    OkHttpBasedRxHttpClient(final String host, final int port, final Optional<String> certPath) {
        final String scheme = certPath.isPresent() ? "https" : "http";
        baseApiUrl = scheme + "://" + host + ":" + port;
        logger.info("Base API uri {}", baseApiUrl);
        if (certPath.isPresent()) {
            client.setSslSocketFactory(new DockerCertificates(Paths.get(certPath.get())).sslContext().getSocketFactory());
        }
        client.setFollowRedirects(true);
        client.setFollowSslRedirects(true);
        client.setReadTimeout(0, TimeUnit.HOURS);
    }

    @Override
    public Observable<String> get(final String endpoint) {
        return get(endpoint, StringResponseTransformer.identityOp());
    }

    @Override
    public <R> Observable<R> get(final String endpoint, final StringResponseTransformer<R> transformer) {
        return get(endpoint, Collections.emptyMap(), transformer);
    }

    @Override
    public <R> Observable<R> get(final String endpoint, final Map<String, String> headers, final StringResponseTransformer<R> transformer) {
        return get(endpoint, headers, transformer.toCollectionTransformer());
    }

    @Override
    public <R> Observable<R> get(final String endpoint, final StringResponseToCollectionTransformer<R> transformer) {
        return get(endpoint, Collections.emptyMap(), transformer);
    }

    @Override
    public <R> Observable<R> get(final String endpoint, final Map<String, String> headers, final StringResponseToCollectionTransformer<R> transformer) {
        final String fullEndpointUrl = fullEndpointUrl(endpoint);
        return Observable.create(subscriber -> {
            if (!subscriber.isUnsubscribed()) {
                try {
                    Response response = makeHttpGetRequest(fullEndpointUrl);
                    if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                        try (ResponseBody body = response.body()) {
                            Collection<R> collection = transformer.apply(body.string());
                            collection.forEach(subscriber::onNext);
                            subscriber.onCompleted();
                        }
                    } else if (response.isSuccessful()) {
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                    }
                } catch (IOException e) {
                    logger.error("Encountered error while making HTTP GET call to '{}'", fullEndpointUrl, e);
                    subscriber.onError(new RestServiceCommunicationException(e));
                }
            }
        });
    }

    @Override
    public <T> Observable<T> get(final String endpoint, final BufferTransformer<T> transformer) {
        return get(endpoint, Collections.emptyMap(), transformer);
    }

    @Override
    public <T> Observable<T> get(final String endpoint, final Map<String, String> headers, final BufferTransformer<T> transformer) {
        final String fullEndpointUrl = fullEndpointUrl(endpoint);
        return Observable.create(subscriber -> {
            try {
                Response response = makeHttpGetRequest(fullEndpointUrl);
                if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                    try (ResponseBody body = response.body()) {
                        BufferedSource source = body.source();
                        while (!source.exhausted() && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(transformer.apply(source.buffer()));
                        }
                        subscriber.onCompleted();
                    }
                } else if (response.isSuccessful()) {
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new RestServiceCommunicationException(e));
            }
        });
    }

    @Override
    public Observable<Buffer> get(final String endpoint, final Map<String, String> headers) {
        return get(endpoint, headers, BufferTransformer.identityOp());
    }

    @Override
    public Observable<Buffer> getAsBuffer(final String endpoint) {
        return get(endpoint, Collections.emptyMap());
    }


    @Override
    public Observable<HttpStatus> getHttpStatus(final String endpointPath) {
        return getWithResponseTransformer(endpointPath, httpStatus());
    }

    @Override
    public <R> Observable<R> getWithResponseTransformer(final String endpoint, final ResponseTransformer<R> transformer) {
        final String url = String.format("%s/%s", baseApiUrl, endpoint);
        Request getRequest = new Request.Builder()
                .header("Content-Type", "application/json")
                .url(url)
                .get()
                .build();
        logger.info("Making GET request to {}", url);
        return Observable.create(subscriber -> {
            try {
                Call call = client.newCall(getRequest);
                Response response = call.execute();
                if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                    subscriber.onNext(transformer.apply(response));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new RestServiceCommunicationException(e));
            }
        });
    }


    @Override
    public <R> Observable<R> post(final String endpoint, final String postBody, final ResponseTransformer<R> transformer) {
        RequestBody requestBody = RequestBody.create(JSON, postBody);
        final String url = String.format("%s/%s", baseApiUrl, endpoint);
        Request getRequest = new Request.Builder()
                .header("Content-Type", "application/json")
                .url(url)
                .post(requestBody)
                .build();
        logger.info("Making POST request to {}", url);
        return Observable.create(subscriber -> {
            try {
                Call call = client.newCall(getRequest);
                Response response = call.execute();
                if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                    subscriber.onNext(transformer.apply(response));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
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
    public Observable<HttpStatus> post(final String endpoint, String body) {
        return post(endpoint, body, httpStatus());
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
    public Observable<Buffer> postBuffer(final String endpoint) {
        return postBuffer(endpoint, EMPTY_BODY, Optional.<AuthConfig>empty());
    }

    @Override
    public Observable<Buffer> postBuffer(final String endpoint, AuthConfig authConfig) {
        return postBuffer(endpoint, EMPTY_BODY, Optional.ofNullable(authConfig));
    }

    @Override
    public Observable<Buffer> postBuffer(final String endpoint, final String postBody, Optional<AuthConfig> authConfig) {
        return Observable.create(subscriber -> {
            try {
                RequestBody requestBody = new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return OCTET;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        logger.info("inside request body");
                    }
                };
                final String url = String.format("%s/%s", baseApiUrl, endpoint);
                Request.Builder requestBuilder = new Request.Builder()
                        .header("Content-Type", "application/json");
                if (authConfig.isPresent()) {
                    requestBuilder
                            .header("X-Registry-Auth", authConfig.get().xAuthHeader());
                }
                Request getRequest = requestBuilder
                        .url(url)
                        .post(requestBody)
                        .build();
                logger.info("Making POST request to {}", url);
                Call call = client.newCall(getRequest);
                Response response = call.execute();
                logger.info("Received response >> {} with headers >> {}", response.code(), response.headers());
                if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                    try (ResponseBody body = response.body()) {
                        BufferedSource source = body.source();
                        while (!source.exhausted() && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(source.buffer());
                        }
                        subscriber.onCompleted();
                    }
                } else {
                    subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (Exception e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new RestServiceCommunicationException(e));
            }
        });
    }


    @Override
    public Observable<HttpStatus> delete(String endpoint) {
        return Observable.create(subscriber -> {
            try {
                final String url = String.format("%s/%s", baseApiUrl, endpoint);
                Request deleteRequest = new Request.Builder()
                        .header("Content-Type", "application/json")
                        .url(url)
                        .delete()
                        .build();
                logger.info("Making DELETE request to {}", url);
                Call call = client.newCall(deleteRequest);
                Response response = call.execute();
                if (response.isSuccessful()) {
                    subscriber.onNext(HttpStatus.of(response.code(), response.message()));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new RestServiceCommunicationException(e));
            }
        });
    }

    @Override
    public <R> Observable<R> postTarStream(final String endpoint, final Path pathToTarArchive, final BufferTransformer<R> transformer) {

        final RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return TAR;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (FileInputStream fin = new FileInputStream(pathToTarArchive.toFile())) {
                    final byte[] buffer = new byte[1024];
                    int n;
                    while (-1 != (n = fin.read(buffer))) {
                        sink.write(buffer, 0, n);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Unable to read tar at %s", pathToTarArchive.toAbsolutePath()), e);
                }
            }
        };

        final String url = String.format("%s/%s", baseApiUrl, endpoint);
        logger.info(String.format("Created request body for %s", url));
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return Observable.create(subscriber ->

                {
                    try {
                        Call call = client.newCall(request);
                        Response response = call.execute();
                        if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                            try (ResponseBody body = response.body()) {
                                BufferedSource source = body.source();
                                while (!source.exhausted() && !subscriber.isUnsubscribed()) {
                                    subscriber.onNext(transformer.apply(source.buffer()));
                                }
                                subscriber.onCompleted();
                            }
                        } else if (response.isSuccessful()) {
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new RestServiceCommunicationException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                        }
                    } catch (IOException e) {
                        logger.error("Encountered error while making {} call", endpoint, e);
                        subscriber.onError(new RestServiceCommunicationException(e));
                    }
                }

        );


    }

    private Response makeHttpGetRequest(final String fullEndpointUrl) throws IOException {
        Request getRequest = new Request.Builder()
                .url(fullEndpointUrl)
                .build();
        logger.info("Making GET request to {}", fullEndpointUrl);
        Call call = client.newCall(getRequest);
        Response response = call.execute();
        logger.debug("Received response with code '{}' and headers '{}'", response.code(), response.headers());
        return response;
    }

    private String fullEndpointUrl(final String endpoint) throws IllegalArgumentException {
        return Optional.ofNullable(endpoint)
                .filter(e -> e.trim().length() > 0)
                .map(e -> e.startsWith("/") ? e : "/" + e)
                .map(e -> baseApiUrl + e)
                .orElseThrow(() -> new IllegalArgumentException("endpoint can't be null or empty"));
    }
}