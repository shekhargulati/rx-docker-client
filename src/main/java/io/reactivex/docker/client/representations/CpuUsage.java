package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CpuUsage {
    @SerializedName("total_usage")
    private Long totalUsage;
    @SerializedName("percpu_usage")
    private List<Long> percpuUsage;
    @SerializedName("usage_in_kernelmode")
    private Long usageInKernelmode;
    @SerializedName("usage_in_usermode")
    private Long usageInUsermode;

    public Long totalUsage() {
        return totalUsage;
    }

    public List<Long> percpuUsage() {
        return percpuUsage;
    }

    public Long usageInKernelmode() {
        return usageInKernelmode;
    }

    public Long usageInUsermode() {
        return usageInUsermode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (percpuUsage == null ? 0 : percpuUsage.hashCode());
        result = prime * result + (totalUsage == null ? 0 : totalUsage.hashCode());
        result = prime * result + (usageInKernelmode == null ? 0 : usageInKernelmode.hashCode());
        result = prime * result + (usageInUsermode == null ? 0 : usageInUsermode.hashCode());
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
        CpuUsage other = (CpuUsage) obj;
        if (percpuUsage == null) {
            if (other.percpuUsage != null) {
                return false;
            }
        } else if (!percpuUsage.equals(other.percpuUsage)) {
            return false;
        }
        if (totalUsage == null) {
            if (other.totalUsage != null) {
                return false;
            }
        } else if (!totalUsage.equals(other.totalUsage)) {
            return false;
        }
        if (usageInKernelmode == null) {
            if (other.usageInKernelmode != null) {
                return false;
            }
        } else if (!usageInKernelmode.equals(other.usageInKernelmode)) {
            return false;
        }
        if (usageInUsermode == null) {
            if (other.usageInUsermode != null) {
                return false;
            }
        } else if (!usageInUsermode.equals(other.usageInUsermode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CpuUsage{" +
                "totalUsage=" + totalUsage +
                ", percpuUsage=" + percpuUsage +
                ", usageInKernelmode=" + usageInKernelmode +
                ", usageInUsermode=" + usageInUsermode +
                '}';
    }
}
