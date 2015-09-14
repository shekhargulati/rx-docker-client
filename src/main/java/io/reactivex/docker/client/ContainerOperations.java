package io.reactivex.docker.client;

import io.reactivex.docker.client.model.DockerContainer;
import rx.Observable;

import java.util.List;

public interface ContainerOperations {
    Observable<DockerContainer> listContainerObs();

    List<DockerContainer> listContainers();
}
