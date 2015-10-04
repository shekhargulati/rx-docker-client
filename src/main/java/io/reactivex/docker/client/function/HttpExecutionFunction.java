package io.reactivex.docker.client.function;

import io.reactivex.docker.client.HttpStatus;
import rx.Observable;

import java.util.function.BiFunction;

@FunctionalInterface
public interface HttpExecutionFunction extends BiFunction<String, String, Observable<HttpStatus>> {
}
