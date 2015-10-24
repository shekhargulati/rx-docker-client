package io.reactivex.docker.client;

import io.reactivex.docker.client.http_client.HttpStatus;
import io.reactivex.docker.client.representations.DockerInfo;
import io.reactivex.docker.client.representations.DockerVersion;
import rx.Observable;

public interface MiscOperations {

    String VERSION_ENDPOINT = "version";
    String INFO_ENDPOINT = "info";
    String CHECK_AUTH_ENDPOINT = "auth";

    Observable<DockerVersion> serverVersionObs();

    DockerVersion serverVersion();

    Observable<DockerInfo> infoObs();

    DockerInfo info();

    HttpStatus checkAuth(AuthConfig authConfig);

    Observable<HttpStatus> checkAuthObs(AuthConfig authConfig);
}
