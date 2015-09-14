package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DockerInfo {
    private int containers;
    private int images;
    private String driver;
    private List<List<String>> driverStatus;
    private String executionDriver;
    private String kernelVersion;
    @SerializedName("NCPU")
    private int cpus;
    private long memTotal;
    private String name;
    @SerializedName("ID")
    private String id;
    private String operatingSystem;
    private Boolean debug;
    @SerializedName("NFd")
    private int fileDescriptors;
    @SerializedName("NGoroutines")
    private int goroutines;
    @SerializedName("NEventsListener")
    private int eventsListener;
    private String initPath;
    private String initSha1;
    private String indexServerAddress;
    private Boolean memoryLimit;
    private Boolean swapLimit;
    private boolean ipv4Forwarding;
    private List<String> labels;
    private String dockerRootDir;

    public int getContainers() {
        return containers;
    }

    public int getImages() {
        return images;
    }

    public String getDriver() {
        return driver;
    }

    public List<List<String>> getDriverStatus() {
        return driverStatus;
    }

    public String getExecutionDriver() {
        return executionDriver;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public int getCpus() {
        return cpus;
    }

    public long getMemTotal() {
        return memTotal;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public Boolean getDebug() {
        return debug;
    }

    public int getFileDescriptors() {
        return fileDescriptors;
    }

    public int getGoroutines() {
        return goroutines;
    }

    public int getEventsListener() {
        return eventsListener;
    }

    public String getInitPath() {
        return initPath;
    }

    public String getInitSha1() {
        return initSha1;
    }

    public String getIndexServerAddress() {
        return indexServerAddress;
    }

    public Boolean getMemoryLimit() {
        return memoryLimit;
    }

    public Boolean getSwapLimit() {
        return swapLimit;
    }

    public boolean isIpv4Forwarding() {
        return ipv4Forwarding;
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getDockerRootDir() {
        return dockerRootDir;
    }
}
