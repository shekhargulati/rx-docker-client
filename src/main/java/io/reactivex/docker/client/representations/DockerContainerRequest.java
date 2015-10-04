package io.reactivex.docker.client.representations;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class DockerContainerRequest {

    @SerializedName("Hostname")
    private String hostname;
    @SerializedName("Domainname")
    private String domainname;
    @SerializedName("User")
    private String user;
    @SerializedName("AttachStdin")
    private Boolean attachStdin;
    @SerializedName("AttachStdout")
    private Boolean attachStdout;
    @SerializedName("AttachStderr")
    private Boolean attachStderr;
    @SerializedName("PortSpecs")
    private List<String> portSpecs;
    @SerializedName("ExposedPorts")
    private List<String> exposedPorts;
    @SerializedName("Tty")
    private Boolean tty;
    @SerializedName("OpenStdin")
    private Boolean openStdin;
    @SerializedName("StdinOnce")
    private Boolean stdinOnce;
    @SerializedName("Env")
    private List<String> env;
    @SerializedName("Cmd")
    private List<String> cmd;
    @SerializedName("Image")
    private String image;
    @SerializedName("Volumes")
    private List<String> volumes;
    @SerializedName("WorkingDir")
    private String workingDir;
    @SerializedName("Entrypoint")
    private List<String> entrypoint;
    @SerializedName("NetworkDisabled")
    private Boolean networkDisabled;
    @SerializedName("OnBuild")
    private List<String> onBuild;
    @SerializedName("Labels")
    private Map<String, String> labels;
    @SerializedName("MacAddress")
    private String macAddress;
    @SerializedName("HostConfig")
    private HostConfig hostConfig;

    DockerContainerRequest(DockerContainerRequestBuilder builder) {
        this.hostname = builder.hostname;
        this.domainname = builder.domainname;
        this.user = builder.user;
        this.attachStdin = builder.attachStdin;
        this.attachStdout = builder.attachStdout;
        this.attachStderr = builder.attachStderr;
        this.portSpecs = builder.portSpecs;
        this.exposedPorts = builder.exposedPorts;
        this.tty = builder.tty;
        this.openStdin = builder.openStdin;
        this.stdinOnce = builder.stdinOnce;
        this.env = builder.env;
        this.cmd = builder.cmd;
        this.image = builder.image;
        this.volumes = builder.volumes;
        this.workingDir = builder.workingDir;
        this.entrypoint = builder.entrypoint;
        this.networkDisabled = builder.networkDisabled;
        this.onBuild = builder.onBuild;
        this.labels = builder.labels;
        this.macAddress = builder.macAddress;
        this.hostConfig = builder.hostConfig;
    }

    public String getHostname() {
        return hostname;
    }

    public String getDomainname() {
        return domainname;
    }

    public String getUser() {
        return user;
    }

    public Boolean getAttachStdin() {
        return attachStdin;
    }

    public Boolean getAttachStdout() {
        return attachStdout;
    }

    public Boolean getAttachStderr() {
        return attachStderr;
    }

    public List<String> getPortSpecs() {
        return portSpecs;
    }

    public List<String> getExposedPorts() {
        return exposedPorts;
    }

    public Boolean getTty() {
        return tty;
    }

    public Boolean getOpenStdin() {
        return openStdin;
    }

    public Boolean getStdinOnce() {
        return stdinOnce;
    }

    public List<String> getEnv() {
        return env;
    }

    public List<String> getCmd() {
        return cmd;
    }

    public String getImage() {
        return image;
    }

    public List<String> getVolumes() {
        return volumes;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public List<String> getEntrypoint() {
        return entrypoint;
    }

    public Boolean getNetworkDisabled() {
        return networkDisabled;
    }

    public List<String> getOnBuild() {
        return onBuild;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public HostConfig getHostConfig() {
        return hostConfig;
    }

    public String toJson() {
        Gson gson = new Gson();
        Type type = new TypeToken<DockerContainerRequest>() {
        }.getType();
        return gson.toJson(this, type);
    }

    @Override
    public String toString() {
        return "DockerContainerRequest{" +
                "hostname='" + hostname + '\'' +
                ", domainname='" + domainname + '\'' +
                ", user='" + user + '\'' +
                ", attachStdin=" + attachStdin +
                ", attachStdout=" + attachStdout +
                ", attachStderr=" + attachStderr +
                ", portSpecs=" + portSpecs +
                ", exposedPorts=" + exposedPorts +
                ", tty=" + tty +
                ", openStdin=" + openStdin +
                ", stdinOnce=" + stdinOnce +
                ", env=" + env +
                ", cmd=" + cmd +
                ", image='" + image + '\'' +
                ", volumes=" + volumes +
                ", workingDir='" + workingDir + '\'' +
                ", entrypoint=" + entrypoint +
                ", networkDisabled=" + networkDisabled +
                ", onBuild=" + onBuild +
                ", labels=" + labels +
                ", macAddress='" + macAddress + '\'' +
                ", hostConfig=" + hostConfig +
                '}';
    }
}
