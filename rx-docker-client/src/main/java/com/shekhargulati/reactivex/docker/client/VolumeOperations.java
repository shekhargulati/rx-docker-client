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

package com.shekhargulati.reactivex.docker.client;

import com.shekhargulati.reactivex.docker.client.representations.Volume;
import com.shekhargulati.reactivex.rxokhttp.QueryParameter;
import rx.Observable;

import java.util.List;

/**
 * This interface declare operations that allow you to work with Docker Volumes https://docs.docker.com/engine/userguide/containers/dockervolumes/.
 */
public interface VolumeOperations {

    String LIST_VOLUMES_ENDPOINT = "/volumes";

    /**
     * List all volumes
     *
     * @return List of Volume
     */
    List<Volume> listAllVolumes();

    /**
     * Returns an Observable of volumes
     *
     * @return Observable of Volume
     */
    Observable<Volume> listAllVolumesObs();


    /**
     * List volumes filtered using query parameters.
     *
     * @param params A vararg of query param
     * @return List of Volume
     */
    List<Volume> listVolumes(QueryParameter... params);

    Observable<Volume> listVolumesObs(QueryParameter... params);


}
