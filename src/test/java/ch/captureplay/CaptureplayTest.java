package ch.captureplay;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.captureplay.annotations.CaptureplayMode;
import ch.captureplay.annotations.CaptureplayCapable;
import ch.captureplay.example.ExampleServiceInterface;
import ch.captureplay.example.ExampleServiceInterfaceImpl;


/**
 * Simple Junit Test to check the functionality of 
 * the CaptureplayManager framework.
 * @author Jonathan Weiss
 *
 */
public class CaptureplayTest {

	protected static final int INIT_ADDITION = 6;
	
	/**
	 * The original service will point during the test run 
	 * to an instance of <code>ExampleServiceInterfaceImpl</code>. 
	 * There wont be a proxy between, because it is not tagged.
	 */
	private ExampleServiceInterface theOriginalInterface;

	@CaptureplayCapable(defaultMode=CaptureplayMode.CAPTURE)
	private ExampleServiceInterface theProxiedInterfaceForCapture;
	
	@CaptureplayCapable(defaultMode=CaptureplayMode.REPLAY)
	private ExampleServiceInterface theProxiedInterfaceForReplay;
	


	@Before
	public void setUp() {
		theOriginalInterface = new ExampleServiceInterfaceImpl(INIT_ADDITION);
		theProxiedInterfaceForCapture = theOriginalInterface;
		theProxiedInterfaceForReplay = theOriginalInterface;
		CaptureplayManager.surroundAnnotatedFieldsWithCaptureplayProxy(this);
		//Here, theProxiedInterface points to a proxy.
		
	}
	
	/**
	 * We check, if the services are correctly initialized. 
	 */
	@Test
	public void initializationCheckTest() {
		Assert.assertNotSame("The proxied service still points to the original service.", theProxiedInterfaceForCapture, theOriginalInterface);
		Assert.assertNotSame("The proxied service still points to the original service.", theProxiedInterfaceForReplay, theOriginalInterface);
		Assert.assertNotSame("The proxies for capture and replay are the same.", theProxiedInterfaceForCapture, theProxiedInterfaceForReplay);
		Assert.assertTrue("The original service is not of type ExampleServiceInterfaceImpl", theOriginalInterface.getClass().equals(ExampleServiceInterfaceImpl.class));
		Assert.assertFalse("The capture proxy service has not been proxied.", theProxiedInterfaceForCapture.getClass().equals(ExampleServiceInterfaceImpl.class));
		Assert.assertFalse("The replay proxy service has not been proxied.", theProxiedInterfaceForReplay.getClass().equals(ExampleServiceInterfaceImpl.class));
	}
	
	/**
	 * Here we test the functionality of the example service.
	 */
	@Test
	public void exampleServiceTest() {
		//Capture with the init addition value from INIT_ADDITION
		Assert.assertEquals(42+INIT_ADDITION, theProxiedInterfaceForCapture.returnWithAddition(42));
		//Replay without changing the original service
		Assert.assertEquals(42+INIT_ADDITION, theProxiedInterfaceForReplay.returnWithAddition(42));

		//Changing the original service
		ExampleServiceInterfaceImpl originalImpl = (ExampleServiceInterfaceImpl) this.theOriginalInterface;
		originalImpl.setAddition(INIT_ADDITION + 5);
		
		//The orignial service returns the new result
		Assert.assertEquals(42+INIT_ADDITION + 5, theOriginalInterface.returnWithAddition(42));
		
		//The proxied service returns still the old result
		Assert.assertEquals(42+INIT_ADDITION, theProxiedInterfaceForReplay.returnWithAddition(42));
	}

}
