package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

public class ContainerStats {
    @SerializedName("read")
    private String read;
    @SerializedName("network")
    private NetworkStats network;
    @SerializedName("memory_stats")
    private MemoryStats memoryStats;
    @SerializedName("cpu_stats")
    private CpuStats cpuStats;
    @SerializedName("precpu_stats")
    private CpuStats precpuStats;

    public String read() {
        return read;
    }

    public NetworkStats network() {
        return network;
    }

    public MemoryStats memoryStats() {
        return memoryStats;
    }

    public CpuStats cpuStats() {
        return cpuStats;
    }

    public CpuStats precpuStats() {
        return precpuStats;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (cpuStats == null ? 0 : cpuStats.hashCode());
        result = prime * result + (memoryStats == null ? 0 : memoryStats.hashCode());
        result = prime * result + (network == null ? 0 : network.hashCode());
        result = prime * result + (precpuStats == null ? 0 : precpuStats.hashCode());
        result = prime * result + (read == null ? 0 : read.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ContainerStats other = (ContainerStats) obj;
        if (cpuStats == null) {
            if (other.cpuStats != null) {
                return false;
            }
        } else if (!cpuStats.equals(other.cpuStats)) {
            return false;
        }
        if (memoryStats == null) {
            if (other.memoryStats != null) {
                return false;
            }
        } else if (!memoryStats.equals(other.memoryStats)) {
            return false;
        }
        if (network == null) {
            if (other.network != null) {
                return false;
            }
        } else if (!network.equals(other.network)) {
            return false;
        }
        if (precpuStats == null) {
            if (other.precpuStats != null) {
                return false;
            }
        } else if (!precpuStats.equals(other.precpuStats)) {
            return false;
        }
        if (read == null) {
            if (other.read != null) {
                return false;
            }
        } else if (!read.equals(other.read)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ContainerStats{" +
                "read='" + read + '\'' +
                ", network=" + network +
                ", memoryStats=" + memoryStats +
                ", cpuStats=" + cpuStats +
                ", precpuStats=" + precpuStats +
                '}';
    }
}