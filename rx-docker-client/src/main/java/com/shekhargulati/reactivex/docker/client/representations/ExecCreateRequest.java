/*
 *
 *  * The MIT License
 *  *
 *  * Copyright 2015 Shekhar Gulati <shekhargulati84@gmail.com>.
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package com.shekhargulati.reactivex.docker.client.representations;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shekhargulati on 09/12/15.
 */
public class ExecCreateRequest {

    @SerializedName("AttachStdin")
    private boolean attachStdin = true;

    @SerializedName("AttachStdout")
    private boolean attachStdout = true;

    @SerializedName("AttachStderr")
    private boolean attachStderr = true;

    @SerializedName("Tty")
    private boolean tty = false;

    @SerializedName("Cmd")
    private List<String> cmd;

    private ExecCreateRequest(List<String> cmd) {
        this.cmd = cmd;
    }

    public static ExecCreateRequest withCmd(List<String> cmd) {
        return new ExecCreateRequest(cmd);
    }

    public boolean isAttachStdin() {
        return attachStdin;
    }

    public boolean isAttachStdout() {
        return attachStdout;
    }

    public boolean isAttachStderr() {
        return attachStderr;
    }

    public boolean isTty() {
        return tty;
    }

    public List<String> getCmd() {
        return cmd;
    }
}
