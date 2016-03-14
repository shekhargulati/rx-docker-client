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

package com.shekhargulati.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class HostConfig {

    @SerializedName("Binds")
    private List<String> binds;
    @SerializedName("ContainerIDFile")
    private String containerIDFile;
    @SerializedName("LxcConf")
    private List<LxcConfParameter> lxcConf;
    @SerializedName("Privileged")
    private Boolean privileged;
    @SerializedName("PortBindings")
    private Map<String, List<PortBinding>> portBindings;
    @SerializedName("Links")
    private List<String> links;
    @SerializedName("PublishAllPorts")
    private Boolean publishAllPorts;
    @SerializedName("Dns")
    private List<String> dns;
    @SerializedName("DnsSearch")
    private List<String> dnsSearch;
    @SerializedName("VolumesFrom")
    private List<String> volumesFrom;
    @SerializedName("NetworkMode")
    private String networkMode;
    @SerializedName("SecurityOpt")
    private List<String> securityOpt;
    @SerializedName("Memory")
    private Long memory;
    @SerializedName("MemorySwap")
    private Long memorySwap;
    @SerializedName("CpuShares")
    private Long cpuShares;
    @SerializedName("CpusetCpus")
    private String cpusetCpus;
    @SerializedName("CgroupParent")
    private String cgroupParent;
    @SerializedName("CapAdd")
    private List<String> capAdd;

    HostConfig(HostConfigBuilder builder) {
        this.binds = builder.binds;
        this.containerIDFile = builder.containerIDFile;
        this.lxcConf = builder.lxcConf;
        this.privileged = builder.privileged;
        this.portBindings = builder.portBindings;
        this.links = builder.links;
        this.publishAllPorts = builder.publishAllPorts;
        this.dns = builder.dns;
        this.dnsSearch = builder.dnsSearch;
        this.volumesFrom = builder.volumesFrom;
        this.networkMode = builder.networkMode;
        this.securityOpt = builder.securityOpt;
        this.memory = builder.memory;
        this.memorySwap = builder.memorySwap;
        this.cpuShares = builder.cpuShares;
        this.cpusetCpus = builder.cpusetCpus;
        this.cgroupParent = builder.cgroupParent;
        this.capAdd = builder.capAdd;
    }

    public List<String> getBinds() {
        return binds;
    }

    public String getContainerIDFile() {
        return containerIDFile;
    }

    public List<LxcConfParameter> getLxcConf() {
        return lxcConf;
    }

    public Boolean getPrivileged() {
        return privileged;
    }

    public Map<String, List<PortBinding>> getPortBindings() {
        return portBindings;
    }

    public List<String> getLinks() {
        return links;
    }

    public Boolean getPublishAllPorts() {
        return publishAllPorts;
    }

    public List<String> getDns() {
        return dns;
    }

    public List<String> getDnsSearch() {
        return dnsSearch;
    }

    public List<String> getVolumesFrom() {
        return volumesFrom;
    }

    public String getNetworkMode() {
        return networkMode;
    }

    public List<String> getSecurityOpt() {
        return securityOpt;
    }

    public Long getMemory() {
        return memory;
    }

    public Long getMemorySwap() {
        return memorySwap;
    }

    public Long getCpuShares() {
        return cpuShares;
    }

    public String getCpusetCpus() {
        return cpusetCpus;
    }

    public String getCgroupParent() {
        return cgroupParent;
    }

    public List<String> getCapAdd() {
        return capAdd;
    }

    public static class LxcConfParameter {

        @SerializedName("Key")
        private String key;
        @SerializedName("Value")
        private String value;

        public LxcConfParameter(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public String key() {
            return key;
        }

        public String value() {
            return value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final LxcConfParameter that = (LxcConfParameter) o;

            if (key != null ? !key.equals(that.key) : that.key != null) {
                return false;
            }
            if (value != null ? !value.equals(that.value) : that.value != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "LxcConfParameter{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
