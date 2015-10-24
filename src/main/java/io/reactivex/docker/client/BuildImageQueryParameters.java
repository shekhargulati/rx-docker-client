package io.reactivex.docker.client;

import java.util.Optional;

public class BuildImageQueryParameters {
    private Optional<String> dockerFile = Optional.empty();
    private Optional<String> remote = Optional.empty();

    private BuildImageQueryParameters() {
    }

    public BuildImageQueryParameters(String dockerFile, String remote) {
        this.dockerFile = Optional.ofNullable(dockerFile);
        this.remote = Optional.ofNullable(remote);
    }

    public String toQueryParameterString() {
        Optional<StringBuilder> queryBuilder = Optional.of(new StringBuilder("?"))
                .flatMap(qb -> dockerFile.map(df -> qb.append("dockerfile=").append(df).append("&")))
                .flatMap(qb -> remote.map(r -> qb.append("remote=").append(r).append("&")));
        return queryBuilder.filter(qb -> qb.toString().endsWith("&")).map(StringBuilder::toString).map(qb -> qb.substring(0, qb.lastIndexOf("&"))).orElse("");
    }

    public static BuildImageQueryParameters withDefaultValues() {
        return new BuildImageQueryParameters();
    }
}

