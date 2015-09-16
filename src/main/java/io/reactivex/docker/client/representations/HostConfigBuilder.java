package io.reactivex.docker.client.representations;

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