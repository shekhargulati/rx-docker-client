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

import java.util.List;
import java.util.Map;

public class HostConfigBuilder {
    List<String> binds;
    String containerIDFile;
    List<HostConfig.LxcConfParameter> lxcConf;
    Boolean privileged;
    Map<String, List<PortBinding>> portBindings;
    List<String> links;
    Boolean publishAllPorts;
    List<String> dns;
    List<String> dnsSearch;
    List<String> volumesFrom;
    String networkMode;
    List<String> securityOpt;
    Long memory;
    Long memorySwap;
    Long cpuShares;
    String cpusetCpus;
    String cgroupParent;

    public HostConfigBuilder setBinds(List<String> binds) {
        this.binds = binds;
        return this;
    }

    public HostConfigBuilder setContainerIDFile(String containerIDFile) {
        this.containerIDFile = containerIDFile;
        return this;
    }

    public HostConfigBuilder setLxcConf(List<HostConfig.LxcConfParameter> lxcConf) {
        this.lxcConf = lxcConf;
        return this;
    }

    public HostConfigBuilder setPrivileged(Boolean privileged) {
        this.privileged = privileged;
        return this;
    }

    public HostConfigBuilder setPortBindings(Map<String, List<PortBinding>> portBindings) {
        this.portBindings = portBindings;
        return this;
    }

    public HostConfigBuilder setLinks(List<String> links) {
        this.links = links;
        return this;
    }

    public HostConfigBuilder setPublishAllPorts(Boolean publishAllPorts) {
        this.publishAllPorts = publishAllPorts;
        return this;
    }

    public HostConfigBuilder setDns(List<String> dns) {
        this.dns = dns;
        return this;
    }

    public HostConfigBuilder setDnsSearch(List<String> dnsSearch) {
        this.dnsSearch = dnsSearch;
        return this;
    }

    public HostConfigBuilder setVolumesFrom(List<String> volumesFrom) {
        this.volumesFrom = volumesFrom;
        return this;
    }

    public HostConfigBuilder setNetworkMode(String networkMode) {
        this.networkMode = networkMode;
        return this;
    }

    public HostConfigBuilder setSecurityOpt(List<String> securityOpt) {
        this.securityOpt = securityOpt;
        return this;
    }

    public HostConfigBuilder setMemory(Long memory) {
        this.memory = memory;
        return this;
    }

    public HostConfigBuilder setMemorySwap(Long memorySwap) {
        this.memorySwap = memorySwap;
        return this;
    }

    public HostConfigBuilder setCpuShares(Long cpuShares) {
        this.cpuShares = cpuShares;
        return this;
    }

    public HostConfigBuilder setCpusetCpus(String cpusetCpus) {
        this.cpusetCpus = cpusetCpus;
        return this;
    }

    public HostConfigBuilder setCgroupParent(String cgroupParent) {
        this.cgroupParent = cgroupParent;
        return this;
    }

    public HostConfig createHostConfig() {
        return new HostConfig(this);
    }
}