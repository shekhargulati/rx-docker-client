/*
 * The MIT License
 *
 * Copyright 2015 Shekhar Gulati <shekhargulati84@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.shekhargulati.reactivex.docker.client;

import com.shekhargulati.reactivex.docker.client.representations.*;
import com.shekhargulati.reactivex.rxokhttp.HttpStatus;
import com.shekhargulati.reactivex.rxokhttp.QueryParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.nio.file.Path;
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
    String CONTAINER_EXPORT_ENDPOINT = CONTAINERS_ENDPOINT + "/export";
    String CONTAINER_STATS_ENDPOINT = CONTAINERS_ENDPOINT + "/stats";
    String CONTAINER_LOGS_ENDPOINT = CONTAINERS_ENDPOINT + "/logs";
    String CONTAINER_CHANGES_ENDPOINT = CONTAINERS_ENDPOINT + "/changes";
    String CONTAINER_RESIZE_ENDPOINT = CONTAINERS_ENDPOINT + "/resize";
    String CONTAINER_PAUSE_ENDPOINT = CONTAINERS_ENDPOINT + "/pause";
    String CONTAINER_UNPAUSE_ENDPOINT = CONTAINERS_ENDPOINT + "/unpause";
    String CONTAINER_ATTACH_ENDPOINT = CONTAINERS_ENDPOINT + "/attach";

    Logger logger = LoggerFactory.getLogger(ContainerOperations.class);

    Observable<DockerContainer> listRunningContainerObs();

    List<DockerContainer> listRunningContainers();

    Observable<DockerContainer> listAllContainersObs();

    List<DockerContainer> listAllContainers();

    List<DockerContainer> listContainers(QueryParameters queryParameters);

    Observable<DockerContainer> listContainersObs(QueryParameters queryParameters);

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

    void exportContainer(String containerId, Path pathToExportTo);

    Observable<ContainerStats> containerStatsObs(String containerId);

    Observable<String> containerLogsObs(String containerId, ContainerLogQueryParameters queryParameters);

    Observable<String> containerLogsObs(String containerId);

    Observable<DockerContainerResponse> createContainerObs(DockerContainerRequest request, String name);

    List<ContainerChange> inspectChangesOnContainerFilesystem(String containerId);

    Observable<ContainerChange> inspectChangesOnContainerFilesystemObs(String containerId);

    HttpStatus resizeContainerTty(String containerId, QueryParameter... queryParameters);

    Observable<HttpStatus> resizeContainerTtyObs(String containerId, QueryParameter... queryParameters);

    HttpStatus pauseContainer(String containerId);

    Observable<HttpStatus> pauseContainerObs(String containerId);

    HttpStatus unpauseContainer(String containerId);

    Observable<HttpStatus> unpauseContainerObs(String containerId);

    Observable<String> attachContainerObs(String containerId, QueryParameter... queryParameters);
}
