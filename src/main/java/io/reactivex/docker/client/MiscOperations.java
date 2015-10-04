package io.reactivex.docker.client;

import io.reactivex.docker.client.representations.DockerInfo;
import io.reactivex.docker.client.representations.DockerVersion;
import rx.Observable;

public interface MiscOperations {

    String VERSION_ENDPOINT = "version";
    String INFO_ENDPOINT = "info";

    Observable<DockerVersion> serverVersionObs();

    DockerVersion serverVersion();

    Observable<DockerInfo> infoObs();

    DockerInfo info();
}
