package io.my.spring.mqtt;

import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author John
 */
public class MqttTemplateTest {
    
    public MqttTemplateTest() {
    }

    /**
     * Test of match method, of class MqttTemplate.
     */
    @Ignore
    @Test
    public void testMatch() {
        System.out.println("match");
        String topic = "/{some_path}/{another}/{dir}/{where}";
        MqttTemplate instance = new MqttTemplate(topic);
        Map<String, String> expResult = null;
        Map<String, String> result = instance.match(topic);
        System.out.println(result);
        assertEquals(expResult, result);        
    }

    /**
     * Test of getTemplate method, of class MqttTemplate.
     */
    @Ignore
    @Test
    public void testGetTemplate() {
        System.out.println("getTemplate");
        MqttTemplate instance = null;
        String expResult = "";
        String result = instance.getTemplate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFilter method, of class MqttTemplate.
     */ 
    @Ignore
    @Test
    public void testGetFilter() {
        System.out.println("getFilter");
        MqttTemplate instance = new MqttTemplate("/{some_path}/{another}/{dir}/{where}");
        String expResult = "";
        String result = instance.getFilter();
        assertEquals(expResult, result);
    }

    /**
     * Test of isValid method, of class MqttTemplate.
     */
    @Ignore
    @Test
    public void testIsValid() {
        System.out.println("isValid");
        MqttTemplate instance = null;
        boolean expResult = false;
        boolean result = instance.isValid();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of buildFilter method, of class MqttTemplate.
     */
    @Test
    public void testBuildFilter() {
        System.out.println("buildFilter");
        String topic = "/{some_path}/{another}/{dir}/{where}/<and>";
        MqttTemplate instance = new MqttTemplate(topic);
        instance.buildFilter();
    }
    
}
