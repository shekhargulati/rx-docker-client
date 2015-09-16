package io.reactivex.docker.client.utils;

import java.util.function.Predicate;

public abstract class Preconditions {

    public static <T> void check(T t, Predicate<T> predicate, String message) throws IllegalArgumentException {
        if (predicate.test(t)) {
            throw new IllegalArgumentException(message);
        }
    }

}
