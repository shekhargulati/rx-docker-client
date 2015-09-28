package io.reactivex.docker.client;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.docker.client.representations.*;
import rx.Observable;

import java.util.List;
import java.util.Optional;

public interface ContainerOperations {
    String CONTAINER_ENDPOINT = "/containers/json%s";
    String CONTAINERS_ENDPOINT = "/containers/%s";
    String CONTAINER_JSON_ENDPOINT = CONTAINERS_ENDPOINT + "/json";
    String CREATE_CONTAINER_ENDPOINT = "/containers/create";
    String CONTAINER_LIST_PROCESS_ENDPOINT = CONTAINERS_ENDPOINT + "/top";
    String CONTAINER_START_ENDPOINT = CONTAINERS_ENDPOINT + "/start";
    String CONTAINER_STOP_ENDPOINT = CONTAINERS_ENDPOINT + "/stop";
    String CONTAINER_RESTART_ENDPOINT = CONTAINERS_ENDPOINT + "/restart";

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

    ProcessListResponse listProcesses(String containerId);

    Observable<ProcessListResponse> listProcessesObs(String containerId);

    Observable<HttpResponseStatus> startContainerObs(String containerId);

    HttpResponseStatus startContainer(String containerId);

    HttpResponseStatus stopContainer(String containerId, final int waitInSecs);

    Observable<HttpResponseStatus> stopContainerObs(String containerId, final int waitInSecs);

    HttpResponseStatus restartContainer(String containerId, int waitInSecs);

    Observable<HttpResponseStatus> restartContainerObs(String containerId, int waitInSecs);
}
