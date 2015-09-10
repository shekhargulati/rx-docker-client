package io.reactivex.docker.client.utils;

@FunctionalInterface
public interface Assert<T> {

    public boolean assertIt(T t);

}
