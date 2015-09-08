package io.reactivex.rxdockerclient;

import static io.reactivex.rxdockerclient.utils.AssertionUtils.check;

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
        check((String str) -> str == null || str.trim() == "", hostPortString, "hostPortString can't be null");
        String endpointWithoutScheme = hostPortString.replaceAll(".*://", "");
        String[] split = endpointWithoutScheme.split(":");
        check(arr -> arr.length != 2, split, String.format("%s should be of format host:port for example 192.168.99.100:2376", hostPortString));
        return new HostAndPort(split[0], Integer.parseInt(split[1]));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


}
