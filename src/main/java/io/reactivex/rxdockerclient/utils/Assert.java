package io.reactivex.rxdockerclient.utils;

@FunctionalInterface
public interface Assert<T> {

    public boolean assertIt(T t);

}
