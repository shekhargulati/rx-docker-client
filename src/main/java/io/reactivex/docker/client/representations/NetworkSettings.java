package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class NetworkSettings {

    @SerializedName("IPAddress")
    private String ipAddress;
    @SerializedName("IPPrefixLen")
    private Integer ipPrefixLen;
    @SerializedName("Gateway")
    private String gateway;
    @SerializedName("Bridge")
    private String bridge;
    @SerializedName("PortMapping")
    private Map<String, Map<String, String>> portMapping;
    @SerializedName("Ports")
    private Map<String, List<PortBinding>> ports;
    @SerializedName("MacAddress")
    private String macAddress;

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getIpPrefixLen() {
        return ipPrefixLen;
    }

    public String getGateway() {
        return gateway;
    }

    public String getBridge() {
        return bridge;
    }

    public Map<String, Map<String, String>> getPortMapping() {
        return portMapping;
    }

    public Map<String, List<PortBinding>> getPorts() {
        return ports;
    }

    public String getMacAddress() {
        return macAddress;
    }
}