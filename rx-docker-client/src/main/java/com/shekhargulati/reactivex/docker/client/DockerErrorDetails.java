package com.shekhargulati.reactivex.docker.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class DockerErrorDetails {
    private ErrorDetails errorDetail;
    private String error;

    public static DockerErrorDetails errorDetails(final String json) {
        Type type = new TypeToken<DockerErrorDetails>() {
        }.getType();
        DockerErrorDetails details = new Gson().fromJson(json, type);
        return details;
    }


    public ErrorDetails getErrorDetail() {
        return errorDetail;
    }

    public String getError() {
        return error;
    }
}


class ErrorDetails {
    private String message;

    public String getMessage() {
        return message;
    }
}