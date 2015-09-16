package io.reactivex.docker.client.utils;

@FunctionalInterface
public interface Precondition<T> {

    public boolean precondition(T t);

}
