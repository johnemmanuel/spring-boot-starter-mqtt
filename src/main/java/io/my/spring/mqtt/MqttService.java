package io.my.spring.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 *
 * @author John
 */
@Slf4j
public class MqttService {

    /**
     *
     * @param context
     * @param client
     * @throws MqttException
     */
    public MqttService(ApplicationContext context, MqttClient client) throws MqttException {
        // Find MQTT Handlers
        Map<String, Object> map = context.getBeansWithAnnotation(MqttHandler.class);
        log.debug("{} MQTT handlers found", map.size());
        // Find Topics
        for (Object object : map.values()) {
            for (Method method : object.getClass().getMethods()) {
                if (method.isAnnotationPresent(MqttTopic.class)) {
                    MqttTopic topic = method.getAnnotation(MqttTopic.class);
                    log.info("Topic Found: {}, QoS: {}", topic.value(), topic.qos());
                    MqttTemplate template = new MqttTemplate(topic.value());
                    client.subscribe(template.getFilter(), new MqttMessageListener(template, object, method));
                }
            }
        }
    }

    static class MqttMessageListener implements IMqttMessageListener {

        private final Object object;
        private final Method method;
        private final MqttTemplate template;

        MqttMessageListener(MqttTemplate template, Object object, Method method) {
            this.template = template;
            this.object = object;
            this.method = method;
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Map<String, String> map = template.match(topic);
            Parameter[] parameters = method.getParameters();
            Object[] params = new Object[method.getParameterCount()];
            for (int i = 0; i < params.length; i++) {
                String name = parameters[i].getName();
                switch (name) {
                    case "topic":
                        params[i] = topic;
                        break;
                    case "message":
                        params[i] = message;
                        break;
                    default:
                        params[i] = map.getOrDefault(parameters[i].getName(), null);
                        break;
                }
            }
            method.invoke(object, params);
        }
    }
}
