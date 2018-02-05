package ch.captureplay.strategy.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import ch.captureplay.example.ExampleServiceInterface;
import ch.captureplay.proxy.MethodInvocation;
import ch.captureplay.strategy.NoCapturedObjectException;

public class AbstractMapCaptureplayerTest {

    protected AbstractMapCaptureplayer captureplayer;
    protected MethodInvocation methodInvocation;
    
    @Before
    public void setUp() throws Throwable {
        methodInvocation = createMockMethodInvocationForTwoArguments("testInvocation");
        captureplayer = new AbstractMapCaptureplayer() {

            @Override
            protected Object getFromMap(MethodInvocation invocation, String key) throws NoCapturedObjectException {
                return null;
            }

            @Override
            public int getMapSize() {
                return 0;
            }

            @Override
            protected void putInMap(MethodInvocation invocation, String key, Object object) {
            }
            
        };
        
    }
    
    /**
     * Create the mock invocation and turn to replay mode.
     */
    private MethodInvocation createMockMethodInvocationForTwoArguments(String value) throws Throwable {
        
        //Create return object mock
        Object returnedObject = new Object();
        
        //Create parameter list mock
        Class<?> [] paramTypes = new Class<?> [] {Integer.class, Object.class};
        
        //Create argument list mock
        int arg1 = 4;
        Object arg2 = new String(value);
        Object [] arguments = new Object [] {arg1, arg2};
        
        //Create declaring class Mock
        //Class declaringClass = SimpleService.class;
        
        //Create method mock
//        Method method = (Method) EasyMock.createMock(Method.class);
//        expect(method.getParameterTypes()).andReturn(paramTypes);
//        expect(method.getName()).andReturn("myMethod");
//        expect(method.getDeclaringClass()).andReturn(declaringClass);
        
        Method method = ExampleServiceInterface.class.getMethod("getObjByTwoArgs", paramTypes);
        
        
        //Create method invocation object mock
        MethodInvocation invocation = (MethodInvocation) EasyMock.createMock(MethodInvocation.class);
        
        expect(invocation.getMethod()).andReturn(method).anyTimes();
        expect(invocation.getArguments()).andReturn(arguments).anyTimes();
        expect(invocation.proceed()).andReturn(returnedObject).anyTimes();

        replay(invocation);
        return invocation;
    }


    @Test
    public void testGetMapName() {
        assertEquals("captureplayer",this.captureplayer.getMapName(methodInvocation));
    }

    
    @Test
    public void testCreateUniqueIdentifier() {
        
        String string1 = "hallo\n test";
        String string2 = "hallo\n test";
        String string3 = "hallo\n prod";
                
        String identifier1 = captureplayer.createUniqueIdentifier(string1);
        String identifier2 = captureplayer.createUniqueIdentifier(string2);
        String identifier3 = captureplayer.createUniqueIdentifier(string3);
        
        assertEquals("1870109118", identifier1);
        //assertEquals("hallo\n test", identifier1);
        assertEquals("identifier1 is not equal identifier2, although they have " +
                "same values: ["+identifier1+"]["+identifier2+"]", 
                identifier1, identifier2);
        assertFalse("identifier1 is equal identifier2, although they have " +
                "different values: ["+identifier1+"]["+identifier2+"]", 
                identifier1.equals(identifier3));
        
    }
    
    @Test
    public void testCreateMapKey() {
        
        Object mapValue1 = new String("myMapValue1");
        //Object mapValue2 = new String("myMapValue2");
        
        Map<String,Object> map1 = new HashMap<String, Object>();
        map1.put("value1", mapValue1);
        map1.put("value2", new Integer(2));
        
        Map<String,Object> map2 = new HashMap<String, Object>();
        map2.put("value2", new Integer(2));
        map2.put("value1", mapValue1);
        
        Map<String,Object> map3 = new HashMap<String, Object>();
        map2.put("value2", new Integer(2));
        
        String identifier1 = captureplayer.createUniqueIdentifier(map1);
        String identifier2 = captureplayer.createUniqueIdentifier(map2);
        String identifier3 = captureplayer.createUniqueIdentifier(map3);
        
        assertEquals("687878698", identifier1);
        //assertEquals("{value1=myMapValue1, value2=2}", identifier1);
        assertEquals("identifier1 is not equal identifier2, although they have " +
                "same values: ["+identifier1+"]["+identifier2+"]", 
                identifier1, identifier2);
        assertFalse("identifier1 is equal identifier2, although they have " +
                "different values: ["+identifier1+"]["+identifier2+"]", 
                identifier1.equals(identifier3));
        
    }
    

    /**
     * @deprecated Inner algorithm has changed.
     */
    @Test
    public void testGetCacheKeyHashkey()throws Throwable {
        MethodInvocation invocation1 = createMockMethodInvocationForTwoArguments("salt n pepper");
        MethodInvocation invocation2 = createMockMethodInvocationForTwoArguments("salt n pepper");
        MethodInvocation invocation3 = createMockMethodInvocationForTwoArguments("salt without pepper");
        assertEquals(captureplayer.createMapKey(invocation1), captureplayer.createMapKey(invocation2));
        assertEquals("ch.captureplay.example.ExampleServiceInterface&getObjByTwoArgs@java.lang.Integer#52-java.lang.Object#501708446", captureplayer.createMapKey(invocation1));
        assertEquals("ch.captureplay.example.ExampleServiceInterface&getObjByTwoArgs@java.lang.Integer#52-java.lang.Object#501708446", captureplayer.createMapKey(invocation2));
        assertEquals("ch.captureplay.example.ExampleServiceInterface&getObjByTwoArgs@java.lang.Integer#52-java.lang.Object#1175399812", captureplayer.createMapKey(invocation3));
        
    }
    
    /**
     * @deprecated Inner algorithm has changed.
     */
    public void testGetCacheKey()throws Throwable {
        MethodInvocation invocation1 = createMockMethodInvocationForTwoArguments("salt n pepper");
        MethodInvocation invocation2 = createMockMethodInvocationForTwoArguments("salt n pepper");
        MethodInvocation invocation3 = createMockMethodInvocationForTwoArguments("salt without pepper");
        assertEquals(captureplayer.createMapKey(invocation1), captureplayer.createMapKey(invocation2));
        assertEquals("ch.captureplay.example.ExampleServiceInterface&getObjByTwoArgs@java.lang.Integer#4-java.lang.Object#salt n pepper", captureplayer.createMapKey(invocation1));
        assertEquals("ch.captureplay.example.ExampleServiceInterface&getObjByTwoArgs@java.lang.Integer#4-java.lang.Object#salt n pepper", captureplayer.createMapKey(invocation2));
        assertEquals("ch.captureplay.example.ExampleServiceInterface&getObjByTwoArgs@java.lang.Integer#4-java.lang.Object#salt without pepper", captureplayer.createMapKey(invocation3));
        
    }

}
