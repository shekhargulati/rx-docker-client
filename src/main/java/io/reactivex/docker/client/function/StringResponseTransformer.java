package io.reactivex.docker.client.function;

import java.util.Arrays;
import java.util.function.Function;

/**
 * StringResponseTransformer is a function that transforms a String response body into a type defined by R
 *
 * @param <R> Type of the transformed object
 */
public interface StringResponseTransformer<R> extends Function<String, R> {

    static StringResponseTransformer<String> identityOp() {
        return t -> t;
    }

    default StringResponseToCollectionTransformer<R> toCollectionTransformer() {
        return str -> Arrays.asList(this.apply(str));
    }

}
