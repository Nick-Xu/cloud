package io.fathom.cloud.secrets.services.ca;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fathomdb.io.IoUtils;
import com.google.common.collect.Lists;

public class CertificateReader {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CertificateReader.class);

    CertificateFactory certificateFactory;

    private CertificateFactory getX509CertificateFactory() {
        if (certificateFactory == null) {
            try {
                certificateFactory = CertificateFactory.getInstance("X509");
            } catch (CertificateException e) {
                throw new IllegalStateException("Error loading X509 provider", e);
            }
        }

        return certificateFactory;
    }

    public X509Certificate[] parse(byte[] data) {
        return parse(new ByteArrayInputStream(data));
    }

    public X509Certificate[] parse(InputStream is) {
        List<X509Certificate> certs = Lists.newArrayList();
        try {
            CertificateFactory x509CertificateFactory = getX509CertificateFactory();
            while (is.available() > 0) {
                X509Certificate cert = (X509Certificate) x509CertificateFactory.generateCertificate(is);
                certs.add(cert);
            }
        } catch (CertificateException ce) {
            throw new IllegalArgumentException("Not an X509 certificate", ce);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading certificates", e);
        }

        return certs.toArray(new X509Certificate[certs.size()]);
    }

    public X509Certificate[] parse(File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        try {
            return parse(fis);
        } finally {
            IoUtils.safeClose(fis);
        }
    }

    public X509Certificate[] parse(String data) {
        if (data == null) {
            return null;
        }

        return parse(data.getBytes());
    }

    public X509Certificate parseFirst(String cert) {
        CertificateReader reader = new CertificateReader();
        X509Certificate[] parsed = reader.parse(cert);
        if (parsed == null || parsed.length == 0) {
            throw new IllegalArgumentException("Cannot parse certificate");
        } else {
            return parsed[0];
        }
    }

    // public static X509Certificate[] parse(String cert) throws OpsException {
    // CertificateReader reader = new CertificateReader();
    // java.security.cert.Certificate[] parsed = reader.parse(cert);
    // if (parsed == null || parsed.length == 0) {
    // throw new OpsException("Cannot parse certificate");
    // } else {
    // X509Certificate[] certificates = new X509Certificate[parsed.length];
    // for (int i = 0; i < parsed.length; i++) {
    // java.security.cert.Certificate cert = parsed[i];
    // if (cert instanceof X509Certificate) {
    // certificates[i] = (X509Certificate) cert;
    // } else {
    // throw new OpsException("Expected X509 certificate, found: " +
    // cert.getClass().getSimpleName());
    // }
    // }
    // return certificates;
    // }
    // }

}
