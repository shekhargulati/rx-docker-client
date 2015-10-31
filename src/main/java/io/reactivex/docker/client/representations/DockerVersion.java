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

package io.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

public class DockerVersion {
    @SerializedName("ApiVersion")
    private String apiVersion;
    @SerializedName("Arch")
    private String arch;
    @SerializedName("GitCommit")
    private String gitCommit;
    @SerializedName("GoVersion")
    private String goVersion;
    @SerializedName("KernelVersion")
    private String kernelVersion;
    @SerializedName("Os")
    private String os;
    @SerializedName("Version")
    private String version;

    public String apiVersion() {
        return apiVersion;
    }

    public String arch() {
        return arch;
    }

    public String gitCommit() {
        return gitCommit;
    }

    public String goVersion() {
        return goVersion;
    }

    public String kernelVersion() {
        return kernelVersion;
    }

    public String os() {
        return os;
    }

    public String version() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DockerVersion version1 = (DockerVersion) o;

        if (apiVersion != null ? !apiVersion.equals(version1.apiVersion)
                : version1.apiVersion != null) {
            return false;
        }
        if (arch != null ? !arch.equals(version1.arch) : version1.arch != null) {
            return false;
        }
        if (gitCommit != null ? !gitCommit.equals(version1.gitCommit) : version1.gitCommit != null) {
            return false;
        }
        if (goVersion != null ? !goVersion.equals(version1.goVersion) : version1.goVersion != null) {
            return false;
        }
        if (kernelVersion != null ? !kernelVersion.equals(version1.kernelVersion)
                : version1.kernelVersion != null) {
            return false;
        }
        if (os != null ? !os.equals(version1.os) : version1.os != null) {
            return false;
        }
        if (version != null ? !version.equals(version1.version) : version1.version != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = apiVersion != null ? apiVersion.hashCode() : 0;
        result = 31 * result + (arch != null ? arch.hashCode() : 0);
        result = 31 * result + (gitCommit != null ? gitCommit.hashCode() : 0);
        result = 31 * result + (goVersion != null ? goVersion.hashCode() : 0);
        result = 31 * result + (kernelVersion != null ? kernelVersion.hashCode() : 0);
        result = 31 * result + (os != null ? os.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DockerVersion{" +
                "apiVersion='" + apiVersion + '\'' +
                ", arch='" + arch + '\'' +
                ", gitCommit='" + gitCommit + '\'' +
                ", goVersion='" + goVersion + '\'' +
                ", kernelVersion='" + kernelVersion + '\'' +
                ", os='" + os + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
