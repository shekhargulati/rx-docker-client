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

public class ContainerArchiveInformation {

    @SerializedName("name")
    private String name;
    @SerializedName("size")
    private int size;
    @SerializedName("mode")
    private long mode;
    @SerializedName("mtime")
    private String mtime;
    @SerializedName("linkTarget")
    private String linkTarget;

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public long getMode() {
        return mode;
    }

    public String getMtime() {
        return mtime;
    }

    public String getLinkTarget() {
        return linkTarget;
    }

    @Override
    public String toString() {
        return "ContainerArchiveInformation{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", mode=" + mode +
                ", mtime='" + mtime + '\'' +
                ", linkTarget='" + linkTarget + '\'' +
                '}';
    }
}
