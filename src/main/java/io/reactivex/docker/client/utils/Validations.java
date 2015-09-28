package io.reactivex.docker.client.utils;

import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Validations {

    public static <T> void validate(T t, Predicate<T> predicate, String message) throws IllegalArgumentException {
        if (predicate.test(t)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void validate(T t, Predicate<T> predicate, Supplier<String> messageSupplier) throws IllegalArgumentException {
        validate(t, predicate, messageSupplier.get());
    }

}
