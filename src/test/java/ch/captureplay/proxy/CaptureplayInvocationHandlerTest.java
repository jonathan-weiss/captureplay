package ch.captureplay.proxy;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import ch.captureplay.annotations.CaptureplayMode;
import ch.captureplay.example.ExampleServiceInterface;
import ch.captureplay.example.ExampleServiceInterfaceImpl;
import ch.captureplay.strategy.MethodCaptureplayer;
import ch.captureplay.strategy.NoCapturedObjectException;

public class CaptureplayInvocationHandlerTest {

	
	protected CaptureplayInvocationHandler handler;
	protected ExampleServiceInterface service;
	
    @Before
    public void setUp() throws Exception {
    	service = new ExampleServiceInterfaceImpl(5);
    }
    
    /**
     * Create the mock invocation and turn to replay mode.
     */
    private MethodInvocation createMockMethodInvocationForTwoArguments(String value) throws Throwable {
        
        //Create return object mock
        Object returnedObject = new String(value);
        
        //Create parameter list mock
        Class<?> [] paramTypes = new Class<?> [] {Integer.class, Object.class};
        
        //Create argument list mock
        int arg1 = 4;
        Object arg2 = new String(value);
        Object [] arguments = new Object [] {arg1, arg2};
        
        //Create declaring class Mock
        //Class declaringClass = SimpleService.class;
        
        //Create method mock
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
	public void testInvokeOffMode() throws Throwable {
		
		MethodCaptureplayer captureplayer = EasyMock.createStrictMock(MethodCaptureplayer.class);
		handler = new CaptureplayInvocationHandler(service, CaptureplayMode.OFF, captureplayer);
		EasyMock.replay(captureplayer);
		MethodInvocation mockMethodInvocation = createMockMethodInvocationForTwoArguments("salt n pepper");

		assertEquals("salt n pepper", handler.invoke(mockMethodInvocation));
	}
	
	@Test
	public void testInvokeCaptureMode()throws Throwable {
		MethodInvocation mockMethodInvocation = createMockMethodInvocationForTwoArguments("salt n pepper");
		
		MethodCaptureplayer captureplayer = EasyMock.createStrictMock(MethodCaptureplayer.class);
		handler = new CaptureplayInvocationHandler(service, CaptureplayMode.CAPTURE, captureplayer);
		captureplayer.capture(mockMethodInvocation, "salt n pepper");
		
		EasyMock.replay(captureplayer);

		assertEquals("salt n pepper", handler.invoke(mockMethodInvocation));
	}

	@Test
	public void testInvokeReplayWithoutCapturing() throws Throwable{
		MethodInvocation mockCaptureMethodInvocation = createMockMethodInvocationForTwoArguments("salt n pepper");
		MethodInvocation mockReplayMethodInvocation = createMockMethodInvocationForTwoArguments("NO salt n pepper");
		
		MethodCaptureplayer captureplayer = EasyMock.createStrictMock(MethodCaptureplayer.class);
		handler = new CaptureplayInvocationHandler(service, CaptureplayMode.CAPTURE, captureplayer);
		captureplayer.capture(mockCaptureMethodInvocation, "salt n pepper");
		EasyMock.expect(captureplayer.replay(mockReplayMethodInvocation)).andThrow(new NoCapturedObjectException(mockReplayMethodInvocation));
		
		EasyMock.replay(captureplayer);

		assertEquals("salt n pepper", handler.invoke(mockCaptureMethodInvocation));
        try {
    		//Test the replay capability
    		handler.mode = CaptureplayMode.REPLAY;
        	handler.invoke(mockReplayMethodInvocation);
            fail("the invoke of replay without a capturing should throw an exception.");
        } catch (NoCapturedObjectException e) {
            // that's what we expected
        }
	}

	@Test
	public void testInvokeCaptureAndReplay() throws Throwable{
		MethodInvocation mockMethodInvocation = createMockMethodInvocationForTwoArguments("salt n pepper");
		
		MethodCaptureplayer captureplayer = EasyMock.createStrictMock(MethodCaptureplayer.class);
		handler = new CaptureplayInvocationHandler(service, CaptureplayMode.CAPTURE, captureplayer);
		captureplayer.capture(mockMethodInvocation, "salt n pepper");
		EasyMock.expect(captureplayer.replay(mockMethodInvocation)).andReturn("salt n pepper");
		EasyMock.replay(captureplayer);

		//Test the capture capability
		assertEquals("salt n pepper", handler.invoke(mockMethodInvocation));
		
		//Test the replay capability
		handler.mode = CaptureplayMode.REPLAY;
		assertEquals("salt n pepper", handler.invoke(mockMethodInvocation));
	}



}
