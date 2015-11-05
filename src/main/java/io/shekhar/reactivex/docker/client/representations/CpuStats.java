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
