package io.my.spring.mqtt;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author John
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
@ConditionalOnClass(MqttClient.class)
public class MqttAutoConfiguration {

    /**
     *
     * @param properties
     * @return
     * @throws MqttException
     */
    @Bean
    @ConditionalOnMissingBean
    public MqttClient mqttClient(MqttProperties properties) throws MqttException {
        log.debug("Trying to connect to MQTT server at: {}", properties.getBroker());
        MqttClient client = new MqttClient(
                properties.getBroker(),
                properties.getClientId(),
                new MemoryPersistence()
        );

        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(properties.getTimeout());
        options.setKeepAliveInterval(properties.getKeepAlive());
        options.setAutomaticReconnect(properties.isAutoReconnect());
        options.setCleanSession(properties.isCleanSession());
        options.setMqttVersion(properties.getVersion());
        if (properties.usernameProvided()) {
            options.setUserName(properties.getUsername());
            options.setPassword(properties.getPassword().toCharArray());
        }

        try {
            SSLSocketFactory socketFactory = null;
            if (properties.bothCaAndClientCertificatesAvailable()) {
                socketFactory = CertificateUtils.getSocketFactory(
                        properties.getAbsoluteCACertFilePath(),
                        properties.getAbsoluteClientCertFilePath(),
                        properties.getAbsoluteKeyFilePath(),
                        properties.getClientKeyFilePassword(),
                        properties.getTlsVersion());
            } else if (properties.isCaCertificateAvailable()) {
                socketFactory = CertificateUtils.getSocketFactory(
                        properties.getAbsoluteCACertFilePath(),
                        properties.getTlsVersion()
                );
            }
            options.setSocketFactory(socketFactory);
        } catch (CertificateException | IOException | KeyManagementException | KeyStoreException
                | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            log.error("Error when creating SSLSocketFactory", ex);
        }

        client.connect(options);
        return client;
    }

    /**
     *
     * @param context
     * @param client
     * @return
     * @throws MqttException
     */
    @Bean
    public MqttService mqttService(ApplicationContext context, MqttClient client) throws MqttException {
        return new MqttService(context, client);
    }
}
