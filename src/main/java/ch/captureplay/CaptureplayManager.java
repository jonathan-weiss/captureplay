package ch.captureplay;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.captureplay.annotations.CaptureplayCapable;
import ch.captureplay.annotations.CaptureplayMode;
import ch.captureplay.proxy.CaptureplayInvocationHandler;
import ch.captureplay.strategy.CaptureplayerFactory;
import ch.captureplay.strategy.MethodCaptureplayer;

/**
 * <p>Static class to activate the captureplay-proxies and manage them:
 * <ul>
 * <li>Surround all the annotated fields of a java object with captureplay-proxy class, so 
 * the fields are capable to capture and replay data. 
 * (see {@link CaptureplayManager#surroundAnnotatedFieldsWithCaptureplayProxy(Object))</li>
 * <li>Switch the mode of all/certain proxy-surrounded fields, e.g. from CAPTURE to REPLAY.
 * (see {@link CaptureplayManager#switchMode(String, CaptureplayMode)}, 
 * {@link CaptureplayManager#switchModeForAll(CaptureplayMode)})
 * </li>
 * </ul>
 * 
 * For some example, how to use this class and how to write a jUnit-Test with captureplay, 
 * see {@link ExampleJunitTest}. 
 * </p>
 *  
 * @see ExampleJunitTest#simpleExample()
 * @see ExampleJunitTest#switchModeOnRuntimeExample()
 * @see ExampleJunitTest#areWeInTheCustomerInfrastructureExample()
 * 
 * @author Jonathan Weiss
 *
 */
public class CaptureplayManager {
	private static Log log = LogFactory.getLog(CaptureplayManager.class);
	
	/**
	 * This map contains all {@link CaptureplayInvocationHandler} to later access them and modificate.
	 * The key of the map are the id's from {@link CaptureplayCapable#value()}.
	 */
	private static Map<String, Set<CaptureplayInvocationHandler>> handlerMap = new HashMap<String, Set<CaptureplayInvocationHandler>>();
	
	/**
	 * Loops through all fields/attributes of <code>instanceToInspect</code> and 
	 * suround all the field with the annotation {@link CaptureplayCapable} with a 
	 * proxy instance (a {@link CaptureplayInvocationHandler}). From now, the annotated 
	 * fields are under controll of the capture/replay functionality. You can switch the 
	 * mode by using the {@link #switchMode(String, CaptureplayMode)} or 
	 * {@link #switchModeForAll(CaptureplayMode)} methods, but 
	 * the {@link CaptureplayCapable#defaultMode()} is preset.
	 * @param instanceToInspect The instance with the annotated fields, i.g. a JUnit-Test object.
	 */
	public static void surroundAnnotatedFieldsWithCaptureplayProxy(Object instanceToInspect) {
		MethodCaptureplayer captureplayer = CaptureplayerFactory.getInstance(); 
		
	    //loop through all fields declared in the interface class  
		for (Field field : instanceToInspect.getClass().getDeclaredFields()) {
	    	  log.debug("Field:"+ field);
	    	  if (field.isAnnotationPresent(CaptureplayCapable.class)) {
	            try {
	            	log.debug(" Annotated Field:"+ field);
	            	
	            	log.debug(" - declaring class:"+ field.getDeclaringClass());
	            	log.debug(" - type:"+ field.getType());
	            	log.debug(" - type:is interface:"+ field.getType().isInterface());
	            	
	            	//This is the real target (non-proxied)
	            	Object targetObject = getFieldValue(instanceToInspect, field);
	            	
	            	String idOfField = field.getAnnotation(CaptureplayCapable.class).value();
	            	CaptureplayMode modeForField = field.getAnnotation(CaptureplayCapable.class).defaultMode();
	            	
	            	CaptureplayInvocationHandler handler = new CaptureplayInvocationHandler(targetObject,modeForField,captureplayer);
	            	addInvocationHandlerToMap(idOfField, handler);
	            	
	            	Class<?> type = field.getType();
	            	
	            	//This is the proxy of the real target
	            	Object proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, handler);
	            	
	            	
	            	//we replace the real target with the proxy. From now on, the proxy is accessed.
	            	setFieldValue(instanceToInspect, field, proxy);
	            } catch (Throwable ex) {
	            	log.error("Error during creating the proxy.", ex);
	            }
	         }
	      }
	}

	/**
	 * Switches the mode of all {@link CaptureplayInvocationHandler} instances.
	 * @param newMode The new mode that has to be set to all handlers.
	 */
	public static void switchModeForAll(CaptureplayMode newMode) {
		for (String id : handlerMap.keySet()) {
			switchMode(id, newMode);
		}
	}
	
	/**
	 * Switches the mode of all {@link CaptureplayInvocationHandler} that have the <code>id</code>.
	 * The id is defined at {@link CaptureplayCapable#value()}. If you didn't give an id, the default 
	 * id is choosen.
	 * @param id The id of the handlers.
	 * @param newMode The new mode that has to be set.
	 */
	public static void switchMode(String id, CaptureplayMode newMode) {
		if(handlerMap.containsKey(id)) {
			for (CaptureplayInvocationHandler invocationHandler : handlerMap.get(id)) {
				invocationHandler.setMode(newMode);
			}
		}
	}
	
	
	
	private static void addInvocationHandlerToMap(String id, CaptureplayInvocationHandler handler) {
		Set<CaptureplayInvocationHandler> handlers = handlerMap.get(id);
		if(handlers == null) {
			handlers = new HashSet<CaptureplayInvocationHandler>();
			handlerMap.put(id, handlers);
		}
		handlers.add(handler);
		
	}
	
	/**
	 * Set the <code>field</code> with <code>value</code> for a certain <code>instance</code>.
	 */
	private static void setFieldValue(Object instance, Field field, Object value) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	try {
			//First "normal" try
    		field.set(instance, value);
		} catch (IllegalAccessException e) {
			boolean isAccessible = field.isAccessible();
			
			if(!isAccessible) {
				try {
					//Second try. Ok, we try to unlock the access
					field.setAccessible(true);
					field.set(instance, value);
					field.setAccessible(isAccessible);
				} catch (SecurityException secEx) {
					//Third try. We try to find a setter and use this setter
					String fieldname = field.getName();
					String methodName = "set" + fieldname.substring(0, 1).toUpperCase() + fieldname.substring(1);
					
					Method theSetter = instance.getClass().getMethod(methodName, field.getType());
					theSetter.invoke(instance, value);
				}
			}
		}
	}
	
	/**
	 * Get the value <code>Object</code> of a <code>field</code> for a certain <code>instance</code>.
	 */
	private static Object getFieldValue(Object instance, Field field) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	try {
			//First "normal" try
    		return field.get(instance);
		} catch (IllegalAccessException e) {
			boolean isAccessible = field.isAccessible();
			
			if(!isAccessible) {
				try {
					Object fieldValue;
					//Second try. Ok, we try to unlock the access
					field.setAccessible(true);
					fieldValue = field.get(instance);
					field.setAccessible(isAccessible);
					
					return fieldValue;
					
				} catch (SecurityException secEx) {
					//Third try. We try to find a getter and use this getter
					String fieldname = field.getName();
					String methodName = "get" + fieldname.substring(0, 1).toUpperCase() + fieldname.substring(1);
					
					Method theGetter = instance.getClass().getMethod(methodName);
					return theGetter.invoke(instance);
				}
			}
			else {
				throw new IllegalAccessException("Method couldn't be called although it is accessible.");
			}
		}		
	}
}
