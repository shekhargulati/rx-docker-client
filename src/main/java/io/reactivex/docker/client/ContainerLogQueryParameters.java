package io.reactivex.docker.client;

import java.beans.BeanInfo;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.beans.Introspector.getBeanInfo;

public class ContainerLogQueryParameters {
    public static final int ONE_MINUTE = 60;
    private boolean stderr = true;
    private boolean stdout = true;
    private boolean timestamps = true;
    private int tail = 10;
    private long since = Instant.now().minusSeconds(ONE_MINUTE).getEpochSecond();
    private Optional<String> queryStringOptional = Optional.empty();

    private static final ContainerLogQueryParameters DEFAULT = new ContainerLogQueryParameters();

    private ContainerLogQueryParameters() {
    }

    public static ContainerLogQueryParameters withDefaultValues() {
        return DEFAULT;
    }

    public ContainerLogQueryParameters(boolean stderr, boolean stdout, boolean timestamps, int tail, long since) {
        this.stderr = stderr;
        this.stdout = stdout;
        this.timestamps = timestamps;
        this.tail = tail;
        this.since = since;
    }

    public boolean isStderr() {
        return stderr;
    }

    public boolean isStdout() {
        return stdout;
    }

    public boolean isTimestamps() {
        return timestamps;
    }

    public int getTail() {
        return tail;
    }

    public long getSince() {
        return since;
    }

    public Optional<String> toQueryString() {
        if (queryStringOptional.isPresent()) {
            return queryStringOptional;
        }
        try {
            BeanInfo beanInfo = getBeanInfo(this.getClass());
            String queryString = Arrays.stream(beanInfo.getPropertyDescriptors()).filter(pd -> !pd.getName().equals("class")).map(pd -> {
                Method reader = pd.getReadMethod();
                try {
                    return pd.getName() + "=" + reader.invoke(this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.joining("&"));
            queryStringOptional = Optional.of("?" + queryString);
            return queryStringOptional;

        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
