package ch.captureplay;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({  
	ch.captureplay.example.ExampleJunitTest.class,
	CaptureplayTest.class,
	ch.captureplay.proxy.CaptureplayInvocationHandlerTest.class,
	ch.captureplay.strategy.MethodCaptureplayerTest.class,
	ch.captureplay.strategy.impl.AbstractMapCaptureplayerTest.class,
	ch.captureplay.strategy.impl.XStreamFrameworkTest.class

})
public class CaptureplayTestsuite {

	// why on earth I need this class, I have no idea! 

}
