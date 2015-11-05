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

package io.shekhar.reactivex.docker.client.ssl;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class DockerCertificates {
    public static final String DEFAULT_CA_CERT_NAME = "ca.pem";
    public static final String DEFAULT_CLIENT_CERT_NAME = "cert.pem";
    public static final String DEFAULT_CLIENT_KEY_NAME = "key.pem";

    private static final char[] KEY_STORE_PASSWORD = "p@ssw0rd".toCharArray();

    private final SSLContext sslContext;

    public DockerCertificates(final Path dockerCertPath) throws DockerCertificateException {
        this(new Builder().dockerCertPath(dockerCertPath));
    }

    private DockerCertificates(final Builder builder) throws DockerCertificateException {
        if ((builder.caCertPath == null) || (builder.clientCertPath == null) ||
                (builder.clientKeyPath == null)) {
            throw new DockerCertificateException(
                    "caCertPath, clientCertPath, and clientKeyPath must all be specified");
        }

        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final Certificate caCert = cf.generateCertificate(Files.newInputStream(builder.caCertPath));
            final Certificate clientCert = cf.generateCertificate(
                    Files.newInputStream(builder.clientCertPath));

            final PEMKeyPair clientKeyPair = (PEMKeyPair) new PEMParser(
                    Files.newBufferedReader(builder.clientKeyPath,
                            Charset.defaultCharset()))
                    .readObject();

            final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(
                    clientKeyPair.getPrivateKeyInfo().getEncoded());
            final KeyFactory kf = KeyFactory.getInstance("RSA");
            final PrivateKey clientKey = kf.generatePrivate(spec);

            final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setEntry("ca", new KeyStore.TrustedCertificateEntry(caCert), null);

            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, KEY_STORE_PASSWORD);
            keyStore.setCertificateEntry("client", clientCert);
            keyStore.setKeyEntry("key", clientKey, KEY_STORE_PASSWORD, new Certificate[]{clientCert});

            this.sslContext = SSLContexts.custom()
                    .loadTrustMaterial(trustStore)
                    .loadKeyMaterial(keyStore, KEY_STORE_PASSWORD)
                    .useTLS()
                    .build();
        } catch (
                CertificateException |
                        IOException |
                        NoSuchAlgorithmException |
                        InvalidKeySpecException |
                        KeyStoreException |
                        UnrecoverableKeyException |
                        KeyManagementException e) {
            throw new DockerCertificateException(e);
        }
    }

    public SSLContext sslContext() {
        return this.sslContext;
    }

    public X509HostnameVerifier hostnameVerifier() {
        return SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Path caCertPath;
        private Path clientKeyPath;
        private Path clientCertPath;

        public Builder dockerCertPath(final Path dockerCertPath) {
            this.caCertPath = dockerCertPath.resolve(DEFAULT_CA_CERT_NAME);
            this.clientKeyPath = dockerCertPath.resolve(DEFAULT_CLIENT_KEY_NAME);
            this.clientCertPath = dockerCertPath.resolve(DEFAULT_CLIENT_CERT_NAME);

            return this;
        }

        public Builder caCertPath(final Path caCertPath) {
            this.caCertPath = caCertPath;
            return this;
        }

        public Builder clientKeyPath(final Path clientKeyPath) {
            this.clientKeyPath = clientKeyPath;
            return this;
        }

        public Builder clientCertPath(final Path clientCertPath) {
            this.clientCertPath = clientCertPath;
            return this;
        }

        public DockerCertificates build() throws DockerCertificateException {
            return new DockerCertificates(this);
        }
    }
}
