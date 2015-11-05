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

package io.shekhar.reactivex.docker.client.http_client;

import com.squareup.okhttp.*;
import io.shekhar.reactivex.docker.client.AuthConfig;
import io.shekhar.reactivex.docker.client.DockerStreamResponseException;
import io.shekhar.reactivex.docker.client.function.*;
import io.shekhar.reactivex.docker.client.ssl.DockerCertificates;
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
import java.util.function.Predicate;

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
                        subscriber.onError(new ServiceException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                    }
                } catch (IOException e) {
                    logger.error("Encountered error while making HTTP GET call to '{}'", fullEndpointUrl, e);
                    subscriber.onError(new ServiceException(e));
                }
            }
        });
    }

    @Override
    public <T> Observable<T> getResponseStream(final String endpoint, final StringResponseTransformer<T> transformer) {
        return getResponseStream(endpoint, Collections.emptyMap(), transformer);
    }

    @Override
    public <T> Observable<T> getResponseStream(final String endpoint, final Map<String, String> headers, final StringResponseTransformer<T> transformer) {
        final String fullEndpointUrl = fullEndpointUrl(endpoint);
        return Observable.create(subscriber -> {
            try {
                Response response = makeHttpGetRequest(fullEndpointUrl);
                if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                    try (ResponseBody body = response.body()) {
                        BufferedSource source = body.source();
                        while (!source.exhausted() && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(transformer.apply(source.buffer().readUtf8()));
                        }
                        subscriber.onCompleted();
                    }
                } else if (response.isSuccessful()) {
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new ServiceException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new ServiceException(e));
            }
        });
    }

    @Override
    public Observable<String> getResponseStream(final String endpoint, final Map<String, String> headers) {
        return getResponseStream(endpoint, headers, StringResponseTransformer.identityOp());
    }

    @Override
    public Observable<Buffer> getResponseBufferStream(final String endpoint) {
        final String fullEndpointUrl = fullEndpointUrl(endpoint);
        return Observable.create(subscriber -> {
            try {
                Response response = makeHttpGetRequest(fullEndpointUrl);
                if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                    try (ResponseBody body = response.body()) {
                        BufferedSource source = body.source();
                        while (!source.exhausted() && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(source.buffer());
                        }
                        subscriber.onCompleted();
                    }
                } else if (response.isSuccessful()) {
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new ServiceException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new ServiceException(e));
            }
        });
    }

    @Override
    public Observable<String> getResponseStream(final String endpoint) {
        return getResponseStream(endpoint, Collections.emptyMap());
    }

    @Override
    public Observable<HttpStatus> getResponseHttpStatus(final String endpointPath) {
        return get(endpointPath, ResponseTransformer.httpStatus());
    }

    @Override
    public <R> Observable<R> get(final String endpoint, final ResponseTransformer<R> transformer) {
        final String fullEndpointUrl = fullEndpointUrl(endpoint);
        return Observable.create(subscriber -> {
            try {
                Response response = makeHttpGetRequest(fullEndpointUrl);
                if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                    subscriber.onNext(transformer.apply(response));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new ServiceException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new ServiceException(e));
            }
        });
    }

    @Override
    public Observable<HttpStatus> post(final String endpoint) {
        return post(endpoint, EMPTY_BODY, ResponseTransformer.httpStatus());
    }

    @Override
    public Observable<HttpStatus> post(final String endpoint, String body) {
        return post(endpoint, body, ResponseTransformer.httpStatus());
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
    public <R> Observable<R> post(final String endpoint, final String postBody, final ResponseTransformer<R> transformer) {
        final String fullEndpointUrl = fullEndpointUrl(endpoint);
        return Observable.create(subscriber -> {
            try {
                Response response = makeHttpPostRequest(fullEndpointUrl, postBody);
                if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                    subscriber.onNext(transformer.apply(response));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new ServiceException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new ServiceException(e));
            }
        });
    }

    @Override
    public Observable<String> postAndReceiveResponse(final String endpoint) {
        return postAndReceiveResponse(endpoint, EMPTY_BODY, Optional.<AuthConfig>empty(), t -> false);
    }

    @Override
    public Observable<String> postAndReceiveResponse(final String endpoint, AuthConfig authConfig, Predicate<String> errorChecker) {
        return postAndReceiveResponse(endpoint, EMPTY_BODY, Optional.ofNullable(authConfig), errorChecker);
    }

    @Override
    public Observable<String> postAndReceiveResponse(final String endpoint, final String postBody, Optional<AuthConfig> authConfig, Predicate<String> errorChecker) {
        final String fullEndpointUrl = fullEndpointUrl(endpoint);
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
                Request.Builder requestBuilder = new Request.Builder()
                        .header("Content-Type", "application/json");
                if (authConfig.isPresent()) {
                    requestBuilder
                            .header("X-Registry-Auth", authConfig.get().xAuthHeader());
                }
                Request postRequest = requestBuilder
                        .url(fullEndpointUrl)
                        .post(requestBody)
                        .build();
                logger.info("Making POST request to {}", fullEndpointUrl);
                Call call = client.newCall(postRequest);
                Response response = call.execute();
                logger.debug("Received response with code '{}' and headers '{}'", response.code(), response.headers());
                if (response.isSuccessful() && !subscriber.isUnsubscribed()) {
                    try (ResponseBody body = response.body()) {
                        BufferedSource source = body.source();
                        while (!source.exhausted() && !subscriber.isUnsubscribed()) {
                            final String responseLine = source.buffer().readUtf8();
                            if (!errorChecker.test(responseLine)) {
                                subscriber.onNext(responseLine);
                            } else {
                                subscriber.onError(new DockerStreamResponseException(responseLine));
                            }
                        }
                        subscriber.onCompleted();
                    }
                } else {
                    subscriber.onError(new ServiceException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (Exception e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new ServiceException(e));
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

        final String fullEndpointUrl = fullEndpointUrl(endpoint);
        return Observable.create(subscriber ->
                {
                    try {
                        Response response = makeHttpPostRequest(fullEndpointUrl, requestBody);
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
                            subscriber.onError(new ServiceException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                        }
                    } catch (IOException e) {
                        logger.error("Encountered error while making {} call", endpoint, e);
                        subscriber.onError(new ServiceException(e));
                    }
                }
        );
    }

    @Override
    public Observable<HttpStatus> delete(final String endpoint) {
        final String fullEndpointUrl = fullEndpointUrl(endpoint);
        return Observable.create(subscriber -> {
            try {
                Response response = makeHttpDeleteRequest(fullEndpointUrl);
                if (response.isSuccessful()) {
                    subscriber.onNext(HttpStatus.of(response.code(), response.message()));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new ServiceException(String.format("Service returned %d with message %s", response.code(), response.message()), response.code(), response.message()));
                }
            } catch (IOException e) {
                logger.error("Encountered error while making {} call", endpoint, e);
                subscriber.onError(new ServiceException(e));
            }
        });
    }

    private Response makeHttpDeleteRequest(String fullEndpointUrl) throws IOException {
        Request deleteRequest = new Request.Builder()
                .header("Content-Type", "application/json")
                .url(fullEndpointUrl)
                .delete()
                .build();
        logger.info("Making DELETE request to {}", fullEndpointUrl);
        Call call = client.newCall(deleteRequest);
        return call.execute();
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

    private Response makeHttpPostRequest(String fullEndpointUrl, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, body);
        return makeHttpPostRequest(fullEndpointUrl, requestBody);
    }

    private Response makeHttpPostRequest(final String fullEndpointUrl, final RequestBody requestBody) throws IOException {
        Request getRequest = new Request.Builder()
                .header("Content-Type", "application/json")
                .url(fullEndpointUrl)
                .post(requestBody)
                .build();
        logger.info("Making POST request to {}", fullEndpointUrl);
        Call call = client.newCall(getRequest);
        return call.execute();
    }
}