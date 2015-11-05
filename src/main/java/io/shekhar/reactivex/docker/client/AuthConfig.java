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

package io.shekhar.reactivex.docker.client;

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

