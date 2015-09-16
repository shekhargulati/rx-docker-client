package io.reactivex.docker.client.utils;

import java.util.Optional;

public abstract class Strings {

    public static boolean isEmptyOrNull(String str) {
        Optional<String> optional = Optional.ofNullable(str);
        return optional.flatMap(s -> s.trim().length() == 0 ? Optional.of(true) : Optional.of(false)).orElse(true);
    }
}
