package io.reactivex.docker.client;

import io.reactivex.docker.client.representations.ContainerInspectResponse;
import io.reactivex.docker.client.representations.DockerContainer;
import io.reactivex.docker.client.representations.DockerContainerRequest;
import io.reactivex.docker.client.representations.DockerContainerResponse;
import rx.Observable;

import java.util.List;
import java.util.Optional;

public interface ContainerOperations {
    String CONTAINER_ENDPOINT = "/containers/json%s";

    Observable<List<DockerContainer>> listRunningContainerObs();

    List<DockerContainer> listRunningContainers();

    Observable<List<DockerContainer>> listAllContainersObs();

    List<DockerContainer> listAllContainers();

    List<DockerContainer> listContainers(QueryParameters queryParameters);

    Observable<List<DockerContainer>> listContainersObs(QueryParameters queryParameters);

    DockerContainerResponse createContainer(DockerContainerRequest request, String name);

    DockerContainerResponse createContainer(DockerContainerRequest request);

    Observable<DockerContainerResponse> createContainerObs(DockerContainerRequest request, Optional<String> name);

    ContainerInspectResponse inspectContainer(String containerId);

    Observable<ContainerInspectResponse> inspectContainerObs(String containerId);
}
