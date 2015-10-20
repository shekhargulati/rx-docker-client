package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DockerImageInspectDetails {
    @SerializedName("Id")
    private String id;
    @SerializedName("Parent")
    private String parent;
    @SerializedName("Comment")
    private String comment;
    @SerializedName("Created")
    private Date created;
    @SerializedName("Container")
    private String container;
    @SerializedName("ContainerConfig")
    private DockerContainerRequest containerConfig;
    @SerializedName("DockerVersion")
    private String dockerVersion;
    @SerializedName("Author")
    private String author;
    @SerializedName("Config")
    private DockerContainerRequest config;
    @SerializedName("Architecture")
    private String architecture;
    @SerializedName("Os")
    private String os;
    @SerializedName("Size")
    private Long size;
    @SerializedName("VirtualSize")
    private Long virtualSize;

    public String getId() {
        return id;
    }

    public String getParent() {
        return parent;
    }

    public String getComment() {
        return comment;
    }

    public Date getCreated() {
        return created;
    }

    public String getContainer() {
        return container;
    }

    public DockerContainerRequest getContainerConfig() {
        return containerConfig;
    }

    public String getDockerVersion() {
        return dockerVersion;
    }

    public String getAuthor() {
        return author;
    }

    public DockerContainerRequest getConfig() {
        return config;
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getOs() {
        return os;
    }

    public Long getSize() {
        return size;
    }

    public Long getVirtualSize() {
        return virtualSize;
    }

    @Override
    public String toString() {
        return "DockerImageInspectDetails{" +
                "id='" + id + '\'' +
                ", parent='" + parent + '\'' +
                ", comment='" + comment + '\'' +
                ", created=" + created +
                ", container='" + container + '\'' +
                ", dockerVersion='" + dockerVersion + '\'' +
                ", author='" + author + '\'' +
                ", architecture='" + architecture + '\'' +
                ", os='" + os + '\'' +
                ", size=" + size +
                ", virtualSize=" + virtualSize +
                '}';
    }
}
