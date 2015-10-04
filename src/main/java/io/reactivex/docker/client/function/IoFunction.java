package io.reactivex.docker.client.function;

import java.io.IOException;

@FunctionalInterface
public interface IoFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws IOException;

    static <T> IoFunction<T, T> identity() {
        return t -> t;
    }
}
