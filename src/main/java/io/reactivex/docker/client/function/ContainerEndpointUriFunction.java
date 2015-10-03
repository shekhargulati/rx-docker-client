package io.reactivex.docker.client.function;

@FunctionalInterface
public interface ContainerEndpointUriFunction extends TriFunction<String, String, String[], String> {

}
