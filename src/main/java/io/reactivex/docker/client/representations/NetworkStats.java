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

public class NetworkStats {
    @SerializedName("rx_bytes")
    private Long rxBytes;
    @SerializedName("rx_packets")
    private Long rxPackets;
    @SerializedName("rx_dropped")
    private Long rxDropped;
    @SerializedName("rx_errors")
    private Long rxErrors;
    @SerializedName("tx_bytes")
    private Long txBytes;
    @SerializedName("tx_packets")
    private Long txPackets;
    @SerializedName("tx_dropped")
    private Long txDropped;
    @SerializedName("tx_errors")
    private Long txErrors;

    public Long rxBytes() {
        return rxBytes;
    }

    public Long rxPackets() {
        return rxPackets;
    }

    public Long rxDropped() {
        return rxDropped;
    }

    public Long rxErrors() {
        return rxErrors;
    }

    public Long txBytes() {
        return txBytes;
    }

    public Long txPackets() {
        return txPackets;
    }

    public Long txDropped() {
        return txDropped;
    }

    public Long txErrors() {
        return txErrors;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (rxBytes == null ? 0 : rxBytes.hashCode());
        result = prime * result + (rxDropped == null ? 0 : rxDropped.hashCode());
        result = prime * result + (rxErrors == null ? 0 : rxErrors.hashCode());
        result = prime * result + (rxPackets == null ? 0 : rxPackets.hashCode());
        result = prime * result + (txBytes == null ? 0 : txBytes.hashCode());
        result = prime * result + (txDropped == null ? 0 : txDropped.hashCode());
        result = prime * result + (txErrors == null ? 0 : txErrors.hashCode());
        result = prime * result + (txPackets == null ? 0 : txPackets.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NetworkStats other = (NetworkStats) obj;
        if (rxBytes == null) {
            if (other.rxBytes != null) {
                return false;
            }
        } else if (!rxBytes.equals(other.rxBytes)) {
            return false;
        }
        if (rxDropped == null) {
            if (other.rxDropped != null) {
                return false;
            }
        } else if (!rxDropped.equals(other.rxDropped)) {
            return false;
        }
        if (rxErrors == null) {
            if (other.rxErrors != null) {
                return false;
            }
        } else if (!rxErrors.equals(other.rxErrors)) {
            return false;
        }
        if (rxPackets == null) {
            if (other.rxPackets != null) {
                return false;
            }
        } else if (!rxPackets.equals(other.rxPackets)) {
            return false;
        }
        if (txBytes == null) {
            if (other.txBytes != null) {
                return false;
            }
        } else if (!txBytes.equals(other.txBytes)) {
            return false;
        }
        if (txDropped == null) {
            if (other.txDropped != null) {
                return false;
            }
        } else if (!txDropped.equals(other.txDropped)) {
            return false;
        }
        if (txErrors == null) {
            if (other.txErrors != null) {
                return false;
            }
        } else if (!txErrors.equals(other.txErrors)) {
            return false;
        }
        if (txPackets == null) {
            if (other.txPackets != null) {
                return false;
            }
        } else if (!txPackets.equals(other.txPackets)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NetworkStats{" +
                "rxBytes=" + rxBytes +
                ", rxPackets=" + rxPackets +
                ", rxDropped=" + rxDropped +
                ", rxErrors=" + rxErrors +
                ", txBytes=" + txBytes +
                ", txPackets=" + txPackets +
                ", txDropped=" + txDropped +
                ", txErrors=" + txErrors +
                '}';
    }
}
