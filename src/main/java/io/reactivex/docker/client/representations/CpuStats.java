package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

public class CpuStats {
    @SerializedName("cpu_usage")
    private CpuUsage cpuUsage;
    @SerializedName("system_cpu_usage")
    Long systemCpuUsage;

    public CpuUsage cpuUsage() {
        return cpuUsage;
    }

    public Long systemCpuUsage() {
        return systemCpuUsage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (cpuUsage == null ? 0 : cpuUsage.hashCode());
        result = prime * result + (systemCpuUsage == null ? 0 : systemCpuUsage.hashCode());
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
        CpuStats other = (CpuStats) obj;
        if (cpuUsage == null) {
            if (other.cpuUsage != null) {
                return false;
            }
        } else if (!cpuUsage.equals(other.cpuUsage)) {
            return false;
        }
        if (systemCpuUsage == null) {
            if (other.systemCpuUsage != null) {
                return false;
            }
        } else if (!systemCpuUsage.equals(other.systemCpuUsage)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CpuStats{" +
                "cpuUsage=" + cpuUsage +
                ", systemCpuUsage=" + systemCpuUsage +
                '}';
    }
}
