package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProcessListResponse {

    @SerializedName("Titles")
    private List<String> titles;

    @SerializedName("Processes")
    private List<List<String>> processes;

    public List<String> getTitles() {
        return titles;
    }

    public List<List<String>> getProcesses() {
        return processes;
    }

    @Override
    public String toString() {
        return "ProcessListResponse{" +
                "titles=" + titles +
                ", processes=" + processes +
                '}';
    }
}
