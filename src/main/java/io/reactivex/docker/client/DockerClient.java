package io.reactivex.docker.client;

public interface DockerClient extends MiscOperations, ContainerOperations {

    /**
     * Builds the client using DOCKER_HOST and DOCKER_CERT_PATH environment variables
     *
     * @return a new instance of RxDockerClient
     */
    public static DockerClient fromDefaultEnv() {
        return new RxDockerClient(System.getenv("DOCKER_HOST"), System.getenv("DOCKER_CERT_PATH"));
    }
}
