package io.my.spring.mqtt;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

/**
 *
 * @author John E
 */
@Slf4j
public class CertificateUtils {

    private CertificateUtils() {
    }

    /**
     *
     * @param caCrtFile
     * @param tlsVersion
     * @return
     * @throws CertificateException
     * @throws IOException
     * @throws KeyManagementException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     */
    public static SSLSocketFactory getSocketFactory(final String caCrtFile, String tlsVersion) throws CertificateException, IOException,
            KeyManagementException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        log.debug("Creating SSL Socket Factory using: \n");
        log.debug("\tCA Cert: {}", caCrtFile);
        Security.addProvider(new BouncyCastleProvider());
        X509Certificate caCert = loadX509CertificatePem(caCrtFile);

        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        SSLContext context = SSLContext.getInstance(tlsVersion);

        context.init(null, tmf.getTrustManagers(), null);
        return context.getSocketFactory();
    }

    /**
     *
     * @param caCrtFile
     * @param clientCrtFile
     * @param keyFile
     * @param password
     * @param tlsVersion
     * @return
     * @throws CertificateException
     * @throws IOException
     * @throws KeyManagementException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     */
    public static SSLSocketFactory getSocketFactory(final String caCrtFile, final String clientCrtFile, final String keyFile,
            final String password, String tlsVersion) throws CertificateException, IOException, KeyManagementException, KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableKeyException {

        log.debug("Creating SSL Socket Factory using: \n");
        log.debug("\tCA Cert: {}", caCrtFile);
        log.debug("\tClient Cert: {}", clientCrtFile);
        log.debug("\tKey File: {}", keyFile);
        log.debug("\tKey File Password: {}", password);

        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate        
        X509Certificate caCert = loadX509CertificatePem(caCrtFile);

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance("JKS");
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        tmf.init(caKs);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");

        // load client certificate
        X509Certificate clientCertificate = loadX509CertificatePem(clientCrtFile);

        // load client private key
        PEMReader reader = new PEMReader(
                new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(keyFile)))),
                () -> password.toCharArray());
        KeyPair key = (KeyPair) reader.readObject();
        reader.close();

        // client key and certificates are sent to server so it can authenticate us
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        ks.setCertificateEntry("certificate", clientCertificate);
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                new java.security.cert.Certificate[]{clientCertificate});
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance(tlsVersion);
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }

    /**
     *
     * @param crtFile
     * @return
     * @throws CertificateException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static X509Certificate loadX509CertificatePem(String crtFile) throws CertificateException,
            FileNotFoundException, IOException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream inStream = new FileInputStream(crtFile);
        Throwable exception = null;
        X509Certificate certificate = null;
        try {
            certificate = (X509Certificate) cf.generateCertificate(inStream);
        } catch (CertificateException crtException) {
            exception = crtException;
            throw crtException;
        } finally {
            if (inStream != null) {
                if (exception != null) {
                    try {
                        inStream.close();
                    } catch (IOException ioException) {
                        exception.addSuppressed(ioException);
                    }
                } else {
                    inStream.close();
                }
            }
        }

        return certificate;
    }

}
