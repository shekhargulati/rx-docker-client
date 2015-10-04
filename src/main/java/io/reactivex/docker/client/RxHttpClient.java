package io.reactivex.docker.client;

import io.reactivex.docker.client.function.JsonTransformer;
import rx.Observable;

import java.util.Optional;

public interface RxHttpClient {

    static RxHttpClient newRxClient(final String host, final int port) {
        return new OkHttpBasedRxHttpClient(host, port);
    }

    static RxHttpClient newRxClient(final String host, final int port, String certPath) {
        return new OkHttpBasedRxHttpClient(host, port, Optional.ofNullable(certPath));
    }

    <R> Observable<R> get(String endpointPath, JsonTransformer<R> transformer);

    Observable<String> get(String endpointPath);
}
