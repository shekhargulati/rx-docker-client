package io.reactivex.docker.client.function;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observable;

import java.util.function.BiFunction;

@FunctionalInterface
public interface HttpExecutionFunction extends BiFunction<String, String, Observable<HttpClientResponse<ByteBuf>>> {
}
