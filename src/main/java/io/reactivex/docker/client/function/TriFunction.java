package io.reactivex.docker.client.function;

import java.util.Objects;
import java.util.function.BiFunction;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    R apply(T t, U u, V v);

    default <X, Y> TriFunction<T, U, V, X> andThen(Y y, BiFunction<? super R, Y, ? extends X> after) {
        Objects.requireNonNull(after);
        return (T t, U u, V v) -> after.apply(apply(t, u, v), y);
    }
}
