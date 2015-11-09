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

import java.util.Collections;
import java.util.HashMap;
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
    Map<String, Map> exposedPorts = new HashMap<>();
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

    public DockerContainerRequestBuilder addExposedPort(String... ports) {
        for (String port : ports) {
            this.exposedPorts.put(port, Collections.emptyMap());
        }
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