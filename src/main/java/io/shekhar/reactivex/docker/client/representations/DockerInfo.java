/*
 * The MIT License
 *
 * Copyright 2015 Shekhar Gulati <shekhargulati84@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.shekhar.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DockerInfo {
    @SerializedName("Containers")
    private int containers;
    @SerializedName("Images")
    private int images;
    @SerializedName("Driver")
    private String storageDriver;
    @SerializedName("DriverStatus")
    private List<List<String>> driverStatus;
    @SerializedName("ExecutionDriver")
    private String executionDriver;
    @SerializedName("KernelVersion")
    private String kernelVersion;
    @SerializedName("NCPU")
    private int cpus;
    @SerializedName("MemTotal")
    private long memTotal;
    @SerializedName("Name")
    private String name;
    @SerializedName("ID")
    private String id;
    @SerializedName("OperatingSystem")
    private String operatingSystem;
    @SerializedName("Debug")
    private Boolean debug;
    @SerializedName("NFd")
    private int fileDescriptors;
    @SerializedName("NGoroutines")
    private int goroutines;
    @SerializedName("NEventsListener")
    private int eventsListener;
    @SerializedName("InitPath")
    private String initPath;
    @SerializedName("InitSha1")
    private String initSha1;
    @SerializedName("IndexServerAddress")
    private String indexServerAddress;
    @SerializedName("MemoryLimit")
    private Boolean memoryLimit;
    @SerializedName("SwapLimit")
    private Boolean swapLimit;
    @SerializedName("IPv4Forwarding")
    private boolean ipv4Forwarding;
    @SerializedName("Labels")
    private List<String> labels;
    @SerializedName("DockerRootDir")
    private String dockerRootDir;

    public int containers() {
        return containers;
    }

    public int images() {
        return images;
    }

    public String storageDriver() {
        return storageDriver;
    }

    public List<List<String>> driverStatus() {
        return driverStatus;
    }

    public int cpus() {
        return cpus;
    }

    public long memTotal() {
        return memTotal;
    }

    public String name() {
        return name;
    }

    public String id() {
        return id;
    }

    public String executionDriver() {
        return executionDriver;
    }

    public String kernelVersion() {
        return kernelVersion;
    }

    public String operatingSystem() {
        return operatingSystem;
    }

    public boolean debug() {
        return debug;
    }

    public int fileDescriptors() {
        return fileDescriptors;
    }

    public int goroutines() {
        return goroutines;
    }

    public int eventsListener() {
        return eventsListener;
    }

    public String initPath() {
        return initPath;
    }

    public String initSha1() {
        return initSha1;
    }

    public String indexServerAddress() {
        return indexServerAddress;
    }

    public boolean memoryLimit() {
        return memoryLimit;
    }

    public boolean swapLimit() {
        return swapLimit;
    }

    public boolean ipv4Forwarding() {
        return ipv4Forwarding;
    }

    public List<String> labels() {
        return labels;
    }

    public String dockerRootDir() {
        return dockerRootDir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DockerInfo info = (DockerInfo) o;

        if (containers != info.containers) {
            return false;
        }
        if (debug != null ? !debug.equals(info.debug)
                : info.debug != null) {
            return false;
        }
        if (eventsListener != info.eventsListener) {
            return false;
        }
        if (fileDescriptors != info.fileDescriptors) {
            return false;
        }
        if (goroutines != info.goroutines) {
            return false;
        }
        if (images != info.images) {
            return false;
        }
        if (executionDriver != null ? !executionDriver.equals(info.executionDriver)
                : info.executionDriver != null) {
            return false;
        }
        if (initPath != null ? !initPath.equals(info.initPath) : info.initPath != null) {
            return false;
        }
        if (kernelVersion != null ? !kernelVersion.equals(info.kernelVersion)
                : info.kernelVersion != null) {
            return false;
        }
        if (storageDriver != null ? !storageDriver.equals(info.storageDriver)
                : info.storageDriver != null) {
            return false;
        }
        if (memoryLimit != null ? !memoryLimit.equals(info.memoryLimit)
                : info.memoryLimit != null) {
            return false;
        }
        if (swapLimit != null ? !swapLimit.equals(info.swapLimit)
                : info.swapLimit != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = containers;
        result = 31 * result + images;
        result = 31 * result + (storageDriver != null ? storageDriver.hashCode() : 0);
        result = 31 * result + (driverStatus != null ? driverStatus.hashCode() : 0);
        result = 31 * result + cpus;
        result = 31 * result + (int) memTotal;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (executionDriver != null ? executionDriver.hashCode() : 0);
        result = 31 * result + (kernelVersion != null ? kernelVersion.hashCode() : 0);
        result = 31 * result + (debug != null ? debug.hashCode() : 0);
        result = 31 * result + fileDescriptors;
        result = 31 * result + goroutines;
        result = 31 * result + eventsListener;
        result = 31 * result + (initPath != null ? initPath.hashCode() : 0);
        result = 31 * result + (initSha1 != null ? initSha1.hashCode() : 0);
        result = 31 * result + (indexServerAddress != null ? indexServerAddress.hashCode() : 0);
        result = 31 * result + (memoryLimit != null ? memoryLimit.hashCode() : 0);
        result = 31 * result + (swapLimit != null ? swapLimit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Info{ containers = %d, images = %d, storageDriver = %s, "
                        + "driverStatus = %s, cpus = %d, memTotal = %d, name = %s, "
                        + "executionDriver = %s, kernelVersion = %s, debug = %b, "
                        + "fileDescriptors = %d, goroutines = %d, eventsListener = %d, "
                        + "initPath = %s, initSha1 = %s, indexServerAddress = %s, "
                        + "memoryLimit = %b, swapLimit = %b",
                containers, images, storageDriver, driverStatus, cpus, memTotal, name,
                executionDriver, kernelVersion, debug, fileDescriptors, goroutines,
                eventsListener, initPath, initSha1, indexServerAddress, memoryLimit,
                swapLimit);
    }
}
