package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

public class MemoryStats {
    @SerializedName("max_usage")
    private Long maxUsage;
    @SerializedName("usage")
    private Long usage;
    @SerializedName("failcnt")
    private Long failcnt;
    @SerializedName("limit")
    private Long limit;

    public Long maxUsage() {
        return maxUsage;
    }

    public Long usage() {
        return usage;
    }

    public Long failcnt() {
        return failcnt;
    }

    public Long limit() {
        return limit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (failcnt == null ? 0 : failcnt.hashCode());
        result = prime * result + (limit == null ? 0 : limit.hashCode());
        result = prime * result + (maxUsage == null ? 0 : maxUsage.hashCode());
        result = prime * result + (usage == null ? 0 : usage.hashCode());
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
        MemoryStats other = (MemoryStats) obj;
        if (failcnt == null) {
            if (other.failcnt != null) {
                return false;
            }
        } else if (!failcnt.equals(other.failcnt)) {
            return false;
        }
        if (limit == null) {
            if (other.limit != null) {
                return false;
            }
        } else if (!limit.equals(other.limit)) {
            return false;
        }
        if (maxUsage == null) {
            if (other.maxUsage != null) {
                return false;
            }
        } else if (!maxUsage.equals(other.maxUsage)) {
            return false;
        }
        if (usage == null) {
            if (other.usage != null) {
                return false;
            }
        } else if (!usage.equals(other.usage)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MemoryStats{" +
                "maxUsage=" + maxUsage +
                ", usage=" + usage +
                ", failcnt=" + failcnt +
                ", limit=" + limit +
                '}';
    }
}
