package io.reactivex.docker.client;

import io.reactivex.docker.client.representations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.List;
import java.util.Optional;

public interface ContainerOperations {
    String CONTAINER_ENDPOINT = "containers/json%s";
    String CONTAINERS_ENDPOINT = "containers/%s";
    String CONTAINER_JSON_ENDPOINT = CONTAINERS_ENDPOINT + "/json";
    String CREATE_CONTAINER_ENDPOINT = "containers/create";
    String CONTAINER_LIST_PROCESS_ENDPOINT = CONTAINERS_ENDPOINT + "/top";
    String CONTAINER_START_ENDPOINT = CONTAINERS_ENDPOINT + "/start";
    String CONTAINER_STOP_ENDPOINT = CONTAINERS_ENDPOINT + "/stop";
    String CONTAINER_RESTART_ENDPOINT = CONTAINERS_ENDPOINT + "/restart";
    String CONTAINER_KILL_ENDPOINT = CONTAINERS_ENDPOINT + "/kill";
    String CONTAINER_REMOVE_ENDPOINT = CONTAINERS_ENDPOINT;
    String CONTAINER_RENAME_ENDPOINT = CONTAINERS_ENDPOINT + "/rename";
    String CONTAINER_WAIT_ENDPOINT = CONTAINERS_ENDPOINT + "/wait";

    Logger logger = LoggerFactory.getLogger(ContainerOperations.class);

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

    Observable<HttpStatus> startContainerObs(String containerId);

    HttpStatus startContainer(String containerId);

    HttpStatus stopContainer(String containerId, final int waitInSecs);

    Observable<HttpStatus> stopContainerObs(String containerId, final int waitInSecs);

    HttpStatus restartContainer(String containerId, int waitInSecs);

    Observable<HttpStatus> restartContainerObs(String containerId, int waitInSecs);

    HttpStatus killRunningContainer(String containerId);

    Observable<HttpStatus> killRunningContainerObs(String containerId);

    default void killAllRunningContainers() {
        listContainers(new QueryParameters()).forEach(container -> {
            String containerId = container.getId();
            logger.info("killing running container with id {}", containerId);
            killRunningContainer(containerId);
        });
    }

    HttpStatus removeContainer(String containerId);

    HttpStatus removeContainer(String containerId, boolean removeVolume, boolean force);

    Observable<HttpStatus> removeContainerObs(String containerId);

    Observable<HttpStatus> removeContainerObs(String containerId, boolean removeVolume, boolean force);

    default void removeAllContainers() {
        listAllContainers().forEach(container -> {
            String containerId = container.getId();
            logger.info("removing container with id {}", containerId);
            removeContainer(containerId, true, true);
        });
    }

    HttpStatus renameContainer(String containerId, String newName);

    Observable<HttpStatus> renameContainerObs(String containerId, String newName);

    HttpStatus waitContainer(String containerId);

    Observable<HttpStatus> waitContainerObs(String containerId);

    void exportContainer(String containerId, String filepath);

    ContainerStats containerStats(String containerId);

    Observable<ContainerStats> containerStatsObs(String containerId);
}
