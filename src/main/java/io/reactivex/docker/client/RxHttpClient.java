package io.reactivex.docker.client;

import io.reactivex.docker.client.function.BufferTransformer;
import io.reactivex.docker.client.function.JsonTransformer;
import io.reactivex.docker.client.function.ResponseBodyTransformer;
import io.reactivex.docker.client.function.ResponseTransformer;
import okio.Buffer;
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

    Observable<Buffer> getBuffer(String endpoint);

    <T> Observable<T> getBuffer(String endpoint, BufferTransformer<T> transformer);

    Observable<String> get(String endpointPath);

    Observable<HttpStatus> post(String endpoint);

    <R> Observable<R> post(String endpoint, String postBody, ResponseTransformer<R> transformer);

    <R> Observable<R> post(String endpoint, ResponseBodyTransformer<R> bodyTransformer);

    <R> Observable<R> post(String endpoint, String postBody, ResponseBodyTransformer<R> bodyTransformer);

    Observable<Buffer> postBuffer(String endpoint);

    Observable<Buffer> postBuffer(String endpoint, String postBody);

    Observable<HttpStatus> delete(final String endpoint);
}
