package io.reactivex.docker.client.function;

import java.util.function.Function;

public interface JsonTransformer<R> extends Function<String, R> {

    static JsonTransformer<String> identity() {
        return t -> t;
    }

}
