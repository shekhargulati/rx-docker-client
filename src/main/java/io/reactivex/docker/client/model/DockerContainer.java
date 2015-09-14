package io.reactivex.docker.client.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DockerContainer {
    @SerializedName("Id")
    private String id;
    @SerializedName("Names")
    private List<String> names;
    @SerializedName("Image")
    private String image;
    @SerializedName("Command")
    private String command;
    @SerializedName("Created")
    private Long created;
    @SerializedName("Status")
    private String status;
    @SerializedName("SizeRw")
    private Long sizeRw;
    @SerializedName("SizeRootFs")
    private Long sizeRootFs;

    public String getId() {
        return id;
    }

    public List<String> getNames() {
        return names;
    }

    public String getImage() {
        return image;
    }

    public String getCommand() {
        return command;
    }

    public Long getCreated() {
        return created;
    }

    public String getStatus() {
        return status;
    }

    public Long getSizeRw() {
        return sizeRw;
    }

    public Long getSizeRootFs() {
        return sizeRootFs;
    }

    @Override
    public String toString() {
        return "DockerContainer{" +
                "id='" + id + '\'' +
                ", names=" + names +
                ", image='" + image + '\'' +
                ", command='" + command + '\'' +
                '}';
    }
}
