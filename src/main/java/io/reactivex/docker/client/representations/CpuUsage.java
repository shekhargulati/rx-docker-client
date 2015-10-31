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
