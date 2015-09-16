package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DockerContainerResponse {

    @SerializedName("Id")
    private String id;
    @SerializedName("Warnings")
    private List<String> warnings;

    public String getId() {
        return id;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    @Override
    public String toString() {
        return "DockerContainerResponse{" +
                "id='" + id + '\'' +
                ", warnings=" + warnings +
                '}';
    }
}
