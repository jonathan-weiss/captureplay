package ch.captureplay.strategy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XStreamFrameworkTest {


    protected XStream xstream;
    
    @Before
    public void setUp() throws Exception {
        //xstream = new XStream();
        xstream = new XStream(new DomDriver()); // does not require XPP3 library
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testXStreamList() throws Exception {

        List<Object> list = new ArrayList<Object>();
        list.add(new String("listValue1"));
        list.add(new String("listValue2"));
        list.add(new Double(3.5));
        
        String xmlString = xstream.toXML(list);
        List<Object> recoveredList = (List<Object>) xstream.fromXML(xmlString);
        
        assertEquals(3, recoveredList.size());
        assertEquals("listValue2", recoveredList.get(1));
    }    


    @Test
    public void testXStreamNonSerializableObject() throws Exception {
        NonSerializableObject nonSerializableObject = new NonSerializableObject();
        nonSerializableObject.name = "nonSerializableObject";
        
        String xmlString = xstream.toXML(nonSerializableObject);
        NonSerializableObject recoveredObject = (NonSerializableObject) xstream.fromXML(xmlString);
        assertEquals("nonSerializableObject", recoveredObject.name);
    }
    

    @SuppressWarnings("unchecked")
    @Test
    public void testXStreamMap() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mapKey4", new String("mapValueA"));
        map.put("mapKey5", new String("mapValueB"));
        map.put("mapKey6", new Integer(3));

        String xmlString = xstream.toXML(map);
        Map<String, Object> recoveredMap = (Map<String, Object>) xstream.fromXML(xmlString);
        
        assertEquals(3, recoveredMap.size());
        assertEquals("mapValueB", recoveredMap.get("mapKey5"));
        assertEquals(3, recoveredMap.get("mapKey6"));
    }
    

    @SuppressWarnings("unchecked")
    @Test
    public void testXStreamCollections() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mapKey1", new String("mapValue1"));
        map.put("mapKey2", new String("mapValue2"));
        map.put("mapKey3", new Integer(3));

        List<Object> list = new ArrayList<Object>();
        list.add(new String("listValue1"));
        list.add(new String("listValue2"));
        list.add(new Double(3.5));
        list.add(map);
        
        String xmlString = xstream.toXML(list);
        List<Object> recoveredList = (List<Object>) xstream.fromXML(xmlString);

        assertEquals(4, recoveredList.size());
        assertEquals("listValue2", recoveredList.get(1));
        assertTrue(recoveredList.get(3) instanceof Map);
        Map<String, Object> recoveredMap = (Map<String, Object>) recoveredList.get(3);
        assertEquals("mapValue2", recoveredMap.get("mapKey2"));
        assertEquals(3, recoveredMap.get("mapKey3"));
    }
        
}
