package io.reactivex.docker.client;

import io.reactivex.docker.client.function.JsonTransformer;
import io.reactivex.docker.client.function.ResponseBodyTransformer;
import io.reactivex.docker.client.function.ResponseTransformer;
import rx.Observable;

import java.util.Optional;

public interface RxHttpClient {

    public static final String EMPTY_BODY = "";

    static RxHttpClient newRxClient(final String host, final int port) {
        return new OkHttpBasedRxHttpClient(host, port);
    }

    static RxHttpClient newRxClient(final String host, final int port, Optional<String> certPath) {
        return new OkHttpBasedRxHttpClient(host, port, certPath);
    }

    <R> Observable<R> get(String endpointPath, JsonTransformer<R> transformer);

    Observable<String> get(String endpointPath);

    Observable<HttpStatus> post(String endpoint);

    <R> Observable<R> post(String endpoint, String postBody, ResponseTransformer<R> transformer);

    <R> Observable<R> post(String endpoint, ResponseBodyTransformer<R> bodyTransformer);

    <R> Observable<R> post(String endpoint, String postBody, ResponseBodyTransformer<R> bodyTransformer);
}
