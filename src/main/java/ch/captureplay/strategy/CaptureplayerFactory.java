package ch.captureplay.strategy;

import ch.captureplay.strategy.impl.XStreamPropertiesCaptureplayer;

/**
 * Factory to return the default <code>MethodCaptureplayer</code> implementation, 
 * defined in the spring application context. 
 * See <pre>classpath:/captureplay-application-context.xml</pre>.
 * 
 * @author Jonathan Weiss
 *
 */
public abstract class CaptureplayerFactory {
	
	private static MethodCaptureplayer newInstance() {
		return new XStreamPropertiesCaptureplayer();
	}
	
	private static MethodCaptureplayer captureplayer = newInstance();
	
	
	/**
	 * Singleton to return an instance of type <code>MethodCaptureplayer</code>.
	 * @see MethodCaptureplayer
	 * @see RuntimeException If the file is not found or the application context isn't defined valid etc.
	 */
	public static MethodCaptureplayer getInstance() throws RuntimeException{
		return captureplayer;
	}
	
	/**
	 * Sets the current captureplayer to null, so it will be 
	 * reinitialized for the next run.
	 */
	public static void resetSingletonInstance() {
		captureplayer = newInstance();
	}


}
