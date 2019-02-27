package io.my.spring.mqtt;

import java.io.IOException;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 *
 * @author John
 */
@Slf4j
@ConfigurationProperties("com.mqtt")
public class MqttProperties {

    @Getter
    @Setter
    private String protocol = "tcp";
    @Getter
    @Setter
    private String brokerUrl = "test.mosquitto.org";
    @Getter
    @Setter
    private int brokerPort = 1883;
    @Getter
    @Setter
    private String clientId = "spring-boot.mqtt.test." + UUID.randomUUID().toString();
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private int timeout;
    @Getter
    @Setter
    private int keepAlive;
    @Getter
    @Setter
    private boolean cleanSession;
    @Getter
    @Setter
    private boolean autoReconnect;

    @Getter
    @Setter
    private int version;
    @Getter
    @Setter
    private boolean caCertificateAvailable;
    @Getter
    @Setter
    private String caCertFilePath;
    @Getter
    @Setter
    private boolean clientCertificateAvailable;
    @Getter
    @Setter
    private String clientCertFilePath;
    @Getter
    @Setter
    private String clientKeyFilePath;
    @Getter
    @Setter
    private String clientKeyFilePassword;
    @Getter
    @Setter
    private String tlsVersion;

    /**
     *
     */
    public MqttProperties() {
    }

    /**
     *
     * @return
     */
    public boolean usernameProvided() {
        return !StringUtils.isEmpty(this.username);
    }

    /**
     *
     * @return
     */
    public boolean bothCaAndClientCertificatesAvailable() {
        return this.caCertificateAvailable && this.clientCertificateAvailable;
    }

    /**
     *
     * @return
     */
    public String getBroker() {
        return String.format("%s://%s:%d", this.protocol, this.brokerUrl, this.brokerPort);
    }

    /**
     *
     * @return
     */
    public String getAbsoluteCACertFilePath() {
        return getClasspathResourceFilePath(caCertFilePath);
    }

    /**
     *
     * @return
     */
    public String getAbsoluteClientCertFilePath() {
        return getClasspathResourceFilePath(clientCertFilePath);
    }

    /**
     *
     * @return
     */
    public String getAbsoluteKeyFilePath() {
        return getClasspathResourceFilePath(clientKeyFilePath);
    }

    private String getClasspathResourceFilePath(String resPath) {
        try {
            if (!StringUtils.isEmpty(resPath)) {
                ClassPathResource res = new ClassPathResource(resPath);
                return res.getFile().getAbsolutePath();
            }
        } catch (IOException ex) {
            log.error("Error when resolving path of file: " + resPath, ex);
        }
        return "";
    }
}
