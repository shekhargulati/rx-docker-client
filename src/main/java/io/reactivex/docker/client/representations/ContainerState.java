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

import java.util.Date;

public class ContainerState {

    @SerializedName("Running")
    private Boolean running;
    @SerializedName("Paused")
    private Boolean paused;
    @SerializedName("Restarting")
    private Boolean restarting;
    @SerializedName("Pid")
    private Integer pid;
    @SerializedName("ExitCode")
    private Integer exitCode;
    @SerializedName("StartedAt")
    private Date startedAt;
    @SerializedName("FinishedAt")
    private Date finishedAt;
    @SerializedName("Error")
    private String error;
    @SerializedName("OOMKilled")
    private Boolean oomKilled;

    public Boolean running() {
        return running;
    }

    public Boolean paused() {
        return paused;
    }

    public Boolean restarting() {
        return restarting;
    }

    public Integer pid() {
        return pid;
    }

    public Integer exitCode() {
        return exitCode;
    }

    public Date startedAt() {
        return startedAt == null ? null : new Date(startedAt.getTime());
    }

    public Date finishedAt() {
        return finishedAt == null ? null : new Date(finishedAt.getTime());
    }

    public String error() {
        return error;
    }

    public Boolean oomKilled() {
        return oomKilled;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ContainerState that = (ContainerState) o;

        if (exitCode != null ? !exitCode.equals(that.exitCode) : that.exitCode != null) {
            return false;
        }
        if (finishedAt != null ? !finishedAt.equals(that.finishedAt) : that.finishedAt != null) {
            return false;
        }
        if (pid != null ? !pid.equals(that.pid) : that.pid != null) {
            return false;
        }
        if (running != null ? !running.equals(that.running) : that.running != null) {
            return false;
        }
        if (paused != null ? !paused.equals(that.paused) : that.paused != null) {
            return false;
        }
        if (restarting != null ? !restarting.equals(that.restarting) : that.restarting != null) {
            return false;
        }
        if (startedAt != null ? !startedAt.equals(that.startedAt) : that.startedAt != null) {
            return false;
        }
        if (error != null ? !error.equals(that.error) : that.error != null) {
            return false;
        }
        if (oomKilled != null ? !oomKilled.equals(that.oomKilled) : that.oomKilled != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = running != null ? running.hashCode() : 0;
        result = 31 * result + (pid != null ? pid.hashCode() : 0);
        result = 31 * result + (paused != null ? paused.hashCode() : 0);
        result = 31 * result + (restarting != null ? restarting.hashCode() : 0);
        result = 31 * result + (exitCode != null ? exitCode.hashCode() : 0);
        result = 31 * result + (startedAt != null ? startedAt.hashCode() : 0);
        result = 31 * result + (finishedAt != null ? finishedAt.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (oomKilled != null ? oomKilled.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "ContainerState{" +
                "running=" + running +
                ", paused=" + paused +
                ", restarting=" + restarting +
                ", pid=" + pid +
                ", exitCode=" + exitCode +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                ", error='" + error + '\'' +
                ", oomKilled=" + oomKilled +
                '}';
    }
}