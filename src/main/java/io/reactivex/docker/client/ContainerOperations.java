package io.reactivex.docker.client;

import io.reactivex.docker.client.representations.DockerContainer;
import rx.Observable;

import java.util.List;

public interface ContainerOperations {
    String CONTAINER_ENDPOINT = "/containers/json%s";

    Observable<List<DockerContainer>> listRunningContainerObs();

    List<DockerContainer> listRunningContainers();

    Observable<List<DockerContainer>> listAllContainersObs();

    List<DockerContainer> listAllContainers();

    List<DockerContainer> listContainers(QueryParameters queryParameters);

    Observable<List<DockerContainer>> listContainersObs(QueryParameters queryParameters);
}
