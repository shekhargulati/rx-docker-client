package io.reactivex.docker.client.function;

import java.util.Collection;
import java.util.function.Function;

public interface StringResponseToCollectionTransformer<T> extends Function<String, Collection<T>> {

}
