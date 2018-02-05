package ch.captureplay.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ch.captureplay.example.ExampleServiceInterface;
import ch.captureplay.example.ExampleServiceInterfaceImpl;
import ch.captureplay.proxy.MethodInvocation;
import ch.captureplay.proxy.impl.MethodInvocationImpl;
import ch.captureplay.strategy.impl.NonSerializableObject;

/**
 * Test for the main functionality of a <code>MethodCaptureplayer</code>.
 * 
 * @author Jonathan Weiss
 *
 */
public class MethodCaptureplayerTest {
	
	protected MethodCaptureplayer captureplayer;
	protected ExampleServiceInterface exampleService;
	
    @Before
    public void setUp() throws Throwable {
    	exampleService = new ExampleServiceInterfaceImpl();
    	captureplayer = CaptureplayerFactory.getInstance();
    	
    }
    
    /**
     * Create an method invocation for a certain target object.
     */
    private MethodInvocation createMethodInvocationForObject(Object target, Object argument) throws Throwable {
        //Create parameter list 
        Class<?> [] methodParamTypes = new Class<?> [] {Object.class};
        Object [] methodArguments = new Object [] {argument};
        Method method = ExampleServiceInterface.class.getMethod("getSimpleObject", methodParamTypes);
    	
    	MethodInvocation invocation = new MethodInvocationImpl(target, method, methodArguments);
        return invocation;
    }
    

	
	@Test
	public void testCaptureAndReplayInteger() throws Throwable {
		Object argument = new Integer(5);
		MethodInvocation invocation = createMethodInvocationForObject(exampleService, argument);
		captureplayer.capture(invocation, argument);
		
		assertEquals(5, ((Integer) captureplayer.replay(invocation)).intValue());
	}

	@Test
	public void testCaptureAndReplaySerializedObjects() throws Throwable {
		Object argument = new Locale("fr");
		MethodInvocation invocation = createMethodInvocationForObject(exampleService, argument);
		captureplayer.capture(invocation, argument);
		
		assertEquals("fr", ((Locale) captureplayer.replay(invocation)).getLanguage());

	}

	@Test
	public void testCaptureAndReplayNonSerializedObjects() throws Throwable {
		NonSerializableObject argument = new NonSerializableObject();
		argument.name = "mein nicht serialisierbars Objekt";
		MethodInvocation invocation = createMethodInvocationForObject(exampleService, argument);
		captureplayer.capture(invocation, argument);
		
		assertEquals("mein nicht serialisierbars Objekt", ((NonSerializableObject) captureplayer.replay(invocation)).name);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCaptureAndReplayList() throws Throwable {
        List<Object> argument = new ArrayList<Object>();
        argument.add(new String("listValue1"));
        argument.add(new String("listValue2"));
        argument.add(new Double(3.5));
        
		MethodInvocation invocation = createMethodInvocationForObject(exampleService, argument);
		captureplayer.capture(invocation, argument);

        
        List<Object> recoveredList = (List<Object>) captureplayer.replay(invocation);
        
        assertEquals(3, recoveredList.size());
        assertEquals("listValue2", recoveredList.get(1));

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCaptureAndReplayMap() throws Throwable {
        Map<String, Object> argument = new HashMap<String, Object>();
        argument.put("mapKey1", new String("mapValue1"));
        argument.put("mapKey2", new String("mapValue2"));
        argument.put("mapKey3", new Integer(3));
        
		MethodInvocation invocation = createMethodInvocationForObject(exampleService, argument);
		captureplayer.capture(invocation, argument);


        Map<String, Object> recoveredMap = (Map<String, Object>) captureplayer.replay(invocation);
        
        assertEquals(3, recoveredMap.size());
        assertEquals("mapValue2", recoveredMap.get("mapKey2"));
        assertEquals(3, recoveredMap.get("mapKey3"));

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCaptureAndReplayCollectionsInCollections() throws Throwable {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mapKey1", new String("mapValue1"));
        map.put("mapKey2", new String("mapValue2"));
        map.put("mapKey3", new Integer(3));

        List<Object> argument = new ArrayList<Object>();
        argument.add(new String("listValue1"));
        argument.add(new String("listValue2"));
        argument.add(new Double(3.5));
        argument.add(map);
        
		MethodInvocation invocation = createMethodInvocationForObject(exampleService, argument);
		captureplayer.capture(invocation, argument);

        List<Object> recoveredList = (List<Object>) captureplayer.replay(invocation);
        
        assertEquals(4, recoveredList.size());
        assertEquals("listValue2", recoveredList.get(1));
        assertTrue(recoveredList.get(3) instanceof Map);
        Map<String, Object> recoveredMap = (Map<String, Object>) recoveredList.get(3);
        assertEquals("mapValue2", recoveredMap.get("mapKey2"));
        assertEquals(3, recoveredMap.get("mapKey3"));


	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCaptureAndReplayTwoCollections() throws Throwable {
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("mapKey1-1", new String("mapValue1a"));
        
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("mapKey2-1", new String("mapValue2a"));
        
		MethodInvocation invocation1 = createMethodInvocationForObject(exampleService, map1);
		captureplayer.capture(invocation1, map1);
		
		MethodInvocation invocation2 = createMethodInvocationForObject(exampleService, map2);
		captureplayer.capture(invocation2, map2);


        Map<String, Object> recoveredMap1 = (Map<String, Object>) captureplayer.replay(invocation1);
        Map<String, Object> recoveredMap2 = (Map<String, Object>) captureplayer.replay(invocation2);
        
        assertEquals(1, recoveredMap1.size());
        assertEquals("mapValue1a", recoveredMap1.get("mapKey1-1"));
        
        assertEquals(1, recoveredMap2.size());
        assertEquals("mapValue2a", recoveredMap2.get("mapKey2-1"));
	}


}
