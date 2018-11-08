package io.my.spring.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class MqttService {

    static class MqttMessageListener implements IMqttMessageListener {

        private Object object;
        private Method method;

        MqttMessageListener(Object object, Method method) {
            this.object = object;
            this.method = method;
        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
            method.invoke(object, topic, message);
        }
    }

    public MqttService(ApplicationContext context, MqttClient mqttClient) throws MqttException {
        //1. 获取所有MqttController注解的对象
        Map<String, Object> map = context.getBeansWithAnnotation(MqttHandler.class);
        log.info("MqttHandler Found: {}", map.size());
        //2. 得到所有对象的Topic注解的方法
        for (Object object: map.values()) {
            for (Method method: object.getClass().getMethods()) {
                if (method.isAnnotationPresent(MqttTopic.class)) {
                    MqttTopic topic = method.getAnnotation(MqttTopic.class);
                    log.info("Topic Found: {}, Qos: {}", topic.value(), topic.qos());
                    mqttClient.subscribe(
                            topic.value(),
                            new MqttMessageListener(object, method));
                }
            }
        }
    }
}


