package io.reactivex.docker.client.representations;

import java.util.List;
import java.util.Map;

public class DockerContainerRequestBuilder {
    String hostname;
    String domainname;
    String user;
    Boolean attachStdin;
    Boolean attachStdout;
    Boolean attachStderr;
    List<String> portSpecs;
    List<String> exposedPorts;
    Boolean tty;
    Boolean openStdin;
    Boolean stdinOnce;
    List<String> env;
    List<String> cmd;
    String image;
    List<String> volumes;
    String workingDir;
    List<String> entrypoint;
    Boolean networkDisabled;
    List<String> onBuild;
    Map<String, String> labels;
    String macAddress;
    HostConfig hostConfig;

    public DockerContainerRequestBuilder setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public DockerContainerRequestBuilder setDomainname(String domainname) {
        this.domainname = domainname;
        return this;
    }

    public DockerContainerRequestBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public DockerContainerRequestBuilder setAttachStdin(Boolean attachStdin) {
        this.attachStdin = attachStdin;
        return this;
    }

    public DockerContainerRequestBuilder setAttachStdout(Boolean attachStdout) {
        this.attachStdout = attachStdout;
        return this;
    }

    public DockerContainerRequestBuilder setAttachStderr(Boolean attachStderr) {
        this.attachStderr = attachStderr;
        return this;
    }

    public DockerContainerRequestBuilder setPortSpecs(List<String> portSpecs) {
        this.portSpecs = portSpecs;
        return this;
    }

    public DockerContainerRequestBuilder setExposedPorts(List<String> exposedPorts) {
        this.exposedPorts = exposedPorts;
        return this;
    }

    public DockerContainerRequestBuilder setTty(Boolean tty) {
        this.tty = tty;
        return this;
    }

    public DockerContainerRequestBuilder setOpenStdin(Boolean openStdin) {
        this.openStdin = openStdin;
        return this;
    }

    public DockerContainerRequestBuilder setStdinOnce(Boolean stdinOnce) {
        this.stdinOnce = stdinOnce;
        return this;
    }

    public DockerContainerRequestBuilder setEnv(List<String> env) {
        this.env = env;
        return this;
    }

    public DockerContainerRequestBuilder setCmd(List<String> cmd) {
        this.cmd = cmd;
        return this;
    }

    public DockerContainerRequestBuilder setImage(String image) {
        this.image = image;
        return this;
    }

    public DockerContainerRequestBuilder setVolumes(List<String> volumes) {
        this.volumes = volumes;
        return this;
    }

    public DockerContainerRequestBuilder setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    public DockerContainerRequestBuilder setEntrypoint(List<String> entrypoint) {
        this.entrypoint = entrypoint;
        return this;
    }

    public DockerContainerRequestBuilder setNetworkDisabled(Boolean networkDisabled) {
        this.networkDisabled = networkDisabled;
        return this;
    }

    public DockerContainerRequestBuilder setOnBuild(List<String> onBuild) {
        this.onBuild = onBuild;
        return this;
    }

    public DockerContainerRequestBuilder setLabels(Map<String, String> labels) {
        this.labels = labels;
        return this;
    }

    public DockerContainerRequestBuilder setMacAddress(String macAddress) {
        this.macAddress = macAddress;
        return this;
    }

    public DockerContainerRequestBuilder setHostConfig(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
        return this;
    }

    public DockerContainerRequest createDockerContainerRequest() {
        return new DockerContainerRequest(this);
    }
}