package io.reactivex.docker.client;

import io.reactivex.docker.client.model.DockerContainer;
import rx.Observable;

import java.util.List;

public interface ContainerOperations {
    Observable<List<DockerContainer>> listRunningContainerObs();

    List<DockerContainer> listRunningContainers();

    Observable<List<DockerContainer>> listAllContainersObs();

    List<DockerContainer> listAllContainers();

    List<DockerContainer> listContainers(QueryParameters queryParameters);

    Observable<List<DockerContainer>> listContainersObs(QueryParameters queryParameters);
}
