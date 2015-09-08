package io.reactivex.rxdockerclient.utils;

import java.util.function.Predicate;

public abstract class AssertionUtils {

    public static <T> void check(Predicate<T> predicate, T t, String message) throws IllegalArgumentException {
        if (predicate.test(t)) {
            throw new IllegalArgumentException(message);
        }
    }

}
