package io.reactivex.docker.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.Base64;

import static com.google.gson.FieldNamingPolicy.UPPER_CAMEL_CASE;

public class AuthConfig {

    @SerializedName("Username")
    private String username;
    @SerializedName("Password")
    private String password;
    @SerializedName("Email")
    private String email;
    @SerializedName("ServerAddress")
    private String serverAddress = "https://index.docker.io/v1/";

    private Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(UPPER_CAMEL_CASE)
            .setPrettyPrinting().create();

    public AuthConfig(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public static AuthConfig authConfig(String username, String password, String email) {
        return new AuthConfig(username, password, email);
    }

    public AuthConfig withServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        return this;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String email() {
        return email;
    }

    public String serverAddress() {
        return serverAddress;
    }

    public String xAuthHeader() {
        return Base64.getEncoder().encodeToString(toJson().getBytes());
    }

    public String toJson() {
        return gson.toJson(this);
    }
}

