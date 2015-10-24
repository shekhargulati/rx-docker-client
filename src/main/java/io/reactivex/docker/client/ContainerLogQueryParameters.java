package io.reactivex.docker.client;

import java.time.Instant;
import java.util.Optional;

public class ContainerLogQueryParameters {
    private boolean stderr = true;
    private boolean stdout = true;
    private boolean timestamps = true;
    private int tail = -1;
    private Optional<Instant> since = Optional.empty();

    private static final ContainerLogQueryParameters DEFAULT = new ContainerLogQueryParameters();

    public static ContainerLogQueryParameters withDefaultValues() {
        return DEFAULT;
    }

    private ContainerLogQueryParameters() {
    }

    public ContainerLogQueryParameters(boolean stderr, boolean stdout, boolean timestamps, int tail, Instant since) {
        this.stderr = stderr;
        this.stdout = stdout;
        this.timestamps = timestamps;
        this.tail = tail;
        this.since = Optional.ofNullable(since);
    }

    public String toQueryParametersString() {
        StringBuilder queryBuilder = new StringBuilder("?");
        queryBuilder.append("stderr=" + stderr);
        queryBuilder.append("&");
        queryBuilder.append("stdout=" + stdout);
        queryBuilder.append("&");
        queryBuilder.append("timestamps=" + timestamps);
        queryBuilder.append("&");
        if (tail < 0) {
            queryBuilder.append("tail=all");
        } else {
            queryBuilder.append("tail=" + tail);
        }
        queryBuilder.append("&");
        if (since.isPresent()) {
            queryBuilder.append("since=" + since.get().getEpochSecond());
        }
        String queryStr = queryBuilder.toString();
        if (queryStr.endsWith("&")) {
            queryStr = queryStr.substring(0, queryStr.lastIndexOf("&"));
        }
        return queryStr;
    }
}
