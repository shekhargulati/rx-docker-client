package io.reactivex.docker.client;

import io.reactivex.docker.client.utils.Strings;
import io.reactivex.docker.client.utils.Validations;

public final class HostAndPort {

    private final String host;
    private final int port;

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static HostAndPort using(String host, int port) {
        return new HostAndPort(host, port);
    }

    public static HostAndPort from(String hostPortString) {
        Validations.validate(hostPortString, Strings::isEmptyOrNull, "hostPortString can't be null");
        String endpointWithoutScheme = hostPortString.replaceAll(".*://", "");
        String[] split = endpointWithoutScheme.split(":");
        Validations.validate(split, arr -> arr.length != 2, String.format("%s should be of format host:port for example 192.168.99.100:2376", hostPortString));
        return new HostAndPort(split[0], Integer.parseInt(split[1]));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


}
