package io.reactivex.docker.client.model;

public class DockerVersion {
    String version;
    String os;
    String kernelVersion;
    String goVersion;
    String gitCommit;
    String arch;
    String apiVersion;
    boolean experimental;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    public String getGoVersion() {
        return goVersion;
    }

    public void setGoVersion(String goVersion) {
        this.goVersion = goVersion;
    }

    public String getGitCommit() {
        return gitCommit;
    }

    public void setGitCommit(String gitCommit) {
        this.gitCommit = gitCommit;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public boolean isExperimental() {
        return experimental;
    }

    public void setExperimental(boolean experimental) {
        this.experimental = experimental;
    }

    @Override
    public String toString() {
        return "DockerVersion{" +
                "version='" + version + '\'' +
                ", os='" + os + '\'' +
                ", kernelVersion='" + kernelVersion + '\'' +
                ", goVersion='" + goVersion + '\'' +
                ", gitCommit='" + gitCommit + '\'' +
                ", arch='" + arch + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                ", experimental=" + experimental +
                '}';
    }
}
