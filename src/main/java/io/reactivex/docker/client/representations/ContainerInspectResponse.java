package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class ContainerInspectResponse {

    @SerializedName("Id")
    private String id;
    @SerializedName("Created")
    private Date created;
    @SerializedName("Path")
    private String path;
    @SerializedName("Args")
    private List<String> args;
    @SerializedName("Config")
    private DockerContainerRequest config;
    @SerializedName("HostConfig")
    private HostConfig hostConfig;
    @SerializedName("State")
    private ContainerState state;
    @SerializedName("Image")
    private String image;
    @SerializedName("NetworkSettings")
    private NetworkSettings networkSettings;
    @SerializedName("ResolvConfPath")
    private String resolvConfPath;
    @SerializedName("HostnamePath")
    private String hostnamePath;
    @SerializedName("HostsPath")
    private String hostsPath;
    @SerializedName("Name")
    private String name;
    @SerializedName("Driver")
    private String driver;
    @SerializedName("ExecDriver")
    private String execDriver;
    @SerializedName("ProcessLabel")
    private String processLabel;
    @SerializedName("MountLabel")
    private String mountLabel;
    @SerializedName("Volumes")
    private Map<String, String> volumes;
    @SerializedName("VolumesRW")
    private Map<String, Boolean> volumesRW;

    public String id() {
        return id;
    }

    public Date created() {
        return created == null ? null : new Date(created.getTime());
    }

    public String path() {
        return path;
    }

    public List<String> args() {
        return args;
    }

    public DockerContainerRequest config() {
        return config;
    }

    public HostConfig hostConfig() {
        return hostConfig;
    }

    public ContainerState state() {
        return state;
    }

    public String image() {
        return image;
    }

    public NetworkSettings networkSettings() {
        return networkSettings;
    }

    public String resolvConfPath() {
        return resolvConfPath;
    }

    public String hostnamePath() {
        return hostnamePath;
    }

    public String hostsPath() {
        return hostsPath;
    }

    public String name() {
        return name;
    }

    public String driver() {
        return driver;
    }

    public String execDriver() {
        return execDriver;
    }

    public String processLabel() {
        return processLabel;
    }

    public String mountLabel() {
        return mountLabel;
    }

    public Map<String, String> volumes() {
        return volumes;
    }

    public Map<String, Boolean> volumesRW() {
        return volumesRW;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ContainerInspectResponse that = (ContainerInspectResponse) o;

        if (args != null ? !args.equals(that.args) : that.args != null) {
            return false;
        }
        if (config != null ? !config.equals(that.config) : that.config != null) {
            return false;
        }
        if (hostConfig != null ? !hostConfig.equals(that.hostConfig) : that.hostConfig != null) {
            return false;
        }
        if (created != null ? !created.equals(that.created) : that.created != null) {
            return false;
        }
        if (driver != null ? !driver.equals(that.driver) : that.driver != null) {
            return false;
        }
        if (execDriver != null ? !execDriver.equals(that.execDriver) : that.execDriver != null) {
            return false;
        }
        if (hostnamePath != null ? !hostnamePath.equals(that.hostnamePath)
                : that.hostnamePath != null) {
            return false;
        }
        if (hostsPath != null ? !hostsPath.equals(that.hostsPath) : that.hostsPath != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (image != null ? !image.equals(that.image) : that.image != null) {
            return false;
        }
        if (mountLabel != null ? !mountLabel.equals(that.mountLabel) : that.mountLabel != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (networkSettings != null ? !networkSettings.equals(that.networkSettings)
                : that.networkSettings != null) {
            return false;
        }
        if (path != null ? !path.equals(that.path) : that.path != null) {
            return false;
        }
        if (processLabel != null ? !processLabel.equals(that.processLabel)
                : that.processLabel != null) {
            return false;
        }
        if (resolvConfPath != null ? !resolvConfPath.equals(that.resolvConfPath)
                : that.resolvConfPath != null) {
            return false;
        }
        if (state != null ? !state.equals(that.state) : that.state != null) {
            return false;
        }
        if (volumes != null ? !volumes.equals(that.volumes) : that.volumes != null) {
            return false;
        }
        if (volumesRW != null ? !volumesRW.equals(that.volumesRW) : that.volumesRW != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (args != null ? args.hashCode() : 0);
        result = 31 * result + (config != null ? config.hashCode() : 0);
        result = 31 * result + (hostConfig != null ? hostConfig.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (networkSettings != null ? networkSettings.hashCode() : 0);
        result = 31 * result + (resolvConfPath != null ? resolvConfPath.hashCode() : 0);
        result = 31 * result + (hostnamePath != null ? hostnamePath.hashCode() : 0);
        result = 31 * result + (hostsPath != null ? hostsPath.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (execDriver != null ? execDriver.hashCode() : 0);
        result = 31 * result + (processLabel != null ? processLabel.hashCode() : 0);
        result = 31 * result + (mountLabel != null ? mountLabel.hashCode() : 0);
        result = 31 * result + (volumes != null ? volumes.hashCode() : 0);
        result = 31 * result + (volumesRW != null ? volumesRW.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContainerInfo{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", path='" + path + '\'' +
                ", args=" + args +
                ", config=" + config +
                ", hostConfig=" + hostConfig +
                ", state=" + state +
                ", image='" + image + '\'' +
                ", networkSettings=" + networkSettings +
                ", resolvConfPath='" + resolvConfPath + '\'' +
                ", hostnamePath='" + hostnamePath + '\'' +
                ", hostsPath='" + hostsPath + '\'' +
                ", name='" + name + '\'' +
                ", driver='" + driver + '\'' +
                ", execDriver='" + execDriver + '\'' +
                ", processLabel='" + processLabel + '\'' +
                ", mountLabel='" + mountLabel + '\'' +
                ", volumes=" + volumes +
                ", volumesRW=" + volumesRW +
                '}';
    }
}
