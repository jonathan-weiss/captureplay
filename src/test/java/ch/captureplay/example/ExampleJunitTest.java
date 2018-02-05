package ch.captureplay.example;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.captureplay.CaptureplayManager;
import ch.captureplay.annotations.CaptureplayCapable;
import ch.captureplay.annotations.CaptureplayMode;


/**
 * Simple Junit Test to check the functionality of 
 * the CaptureplayManager framework.
 * Run this in the debug mode to experience the framework.
 * 
 * 
 * @author Jonathan Weiss
 *
 */
public class ExampleJunitTest {

	/**
	 * The proxied service will point to a proxy, that delegate to 
	 * the real instance (that is the same like the original service).
	 * There is a captureplay proxy between, because it is annotated.
	 */
	@CaptureplayCapable(value="theService", defaultMode=CaptureplayMode.CAPTURE)
	private ExampleServiceInterface theService = new ExampleServiceInterfaceImpl(6);

	/**
	 * A reference to the original service, NOT the proxy.
	 * (because it isn't anotated)
	 */
	private ExampleServiceInterfaceImpl aReferenceToTheTargetService = (ExampleServiceInterfaceImpl) theService;

	
	
	@Before
	public void setUp() {
		//At this point, theService points to the real target (that means "new ExampleServiceInterfaceImpl(6)").
		CaptureplayManager.surroundAnnotatedFieldsWithCaptureplayProxy(this);
		//At this point, theService points to a proxy.
		
		//aReferenceToTheTargetService still points to the real target, because it isn't anotated
	}
	
	/**
	 * Here we test the functionality of the example service.
	 */
	@Test
	public void simpleExample() {
		Assert.assertEquals(4+6, theService.returnWithAddition(4));
	}
	
	/**
	 * Here we test the functionality of the example service with switch of the mode.
	 */
	@Test
	public void switchModeOnRuntimeExample() {
		Assert.assertEquals(6+4, theService.returnWithAddition(4));  //return 6+4 (mode is CAPTURE)
		CaptureplayManager.switchMode("theService", CaptureplayMode.REPLAY);
		Assert.assertEquals(6+4, theService.returnWithAddition(4));  //return 6+4 (mode is REPLAY)
		aReferenceToTheTargetService.setAddition(234);
		Assert.assertEquals(6+4, theService.returnWithAddition(4));  //still returns 6+4, not 234+4 (due to mode is REPLAY)
	}
	
	/**
	 * Here an example how to get data from the customer infrastructure.
	 */
	@Test
	public void areWeInTheCustomerInfrastructureExample() {
		//or in the customer's one.
		boolean weAreInTheCustomerInfrastructure = true;   
		
		
		if(weAreInTheCustomerInfrastructure) {
			//We are in the infrastructure of the customer
			CaptureplayManager.switchMode("theService", CaptureplayMode.CAPTURE);
		}
		else {
			//So, we replay the recorded data and simulate the customer infrastructure.
			CaptureplayManager.switchMode("theService", CaptureplayMode.REPLAY);
		}
		
		Assert.assertEquals(6+4, theService.returnWithAddition(4));  //return 6+4
	}
	

}
