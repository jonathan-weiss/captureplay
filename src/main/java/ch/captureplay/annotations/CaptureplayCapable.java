package ch.captureplay.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ch.captureplay.CaptureplayManager;

/**
 * <p>Tells a service to be proxied/surrounded by captureplay.
 * An proxied object is capable of the capture/replay functionality. 
 * </p>
 * 
 * @author Jonathan Weiss
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CaptureplayCapable {
	
	/**
	 * You can give a key to this object. With the key, you 
	 * are able to change the  mode (e.g. from capture to 
	 * replay) on runtime.
	 * If you don't choose a key, the default key is 
	 * set (empty).
	 * @see CaptureplayManager#switchMode(String, CaptureplayMode)
	 * @see CaptureplayManager#switchModeForAll(CaptureplayMode)
	 */
	String value() default "default"; 
	
	/**
	 * When the object is wrapped by the proxy, the default mode is 
	 * activated. You can switch the mode on runtime with 
	 * {@link CaptureplayManager#switchMode(String, CaptureplayMode)} or 
	 * {@link CaptureplayManager#switchModeForAll(CaptureplayMode)}.
	 */
	CaptureplayMode defaultMode() default CaptureplayMode.SMOOTH; 

}
