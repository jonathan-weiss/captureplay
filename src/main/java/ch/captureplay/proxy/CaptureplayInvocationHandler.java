package ch.captureplay.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.captureplay.annotations.CaptureplayMode;
import ch.captureplay.proxy.impl.MethodInvocationImpl;
import ch.captureplay.strategy.MethodCaptureplayer;
import ch.captureplay.strategy.NoCapturedObjectException;

/**
 * <p>A CaptureplayInvocationHandler is capable to listen for data of a target, store 
 * this data (capture) and replay this data in a later time (replay).
 * It is a handler for the proxy to invocate the target object (capturing) 
 * or return an already stored value from the persistence layer (replay).</p>
 * 
 * @author Jonathan Weiss
 */
public class CaptureplayInvocationHandler implements InvocationHandler {
	private static Log log = LogFactory.getLog(CaptureplayInvocationHandler.class);
	
	/**
	 * The target object who's return values 
	 * are captured or replayed.
	 */
	protected Object target;
	
	/**
	 * The mode for this instance/target.
	 * Can be modified by runtime.
	 */
	protected CaptureplayMode mode;
	
	/**
	 * The capturer/replayer for this instance.
	 * This is the strategy, how data is stored and read from the persistence layer.
	 */
	protected MethodCaptureplayer captureplayer;
	
	/**
	 * Constructor.
	 */
	public CaptureplayInvocationHandler(Object target, CaptureplayMode mode, MethodCaptureplayer captureplayer) {
		this.target = target;
		this.mode = mode;
		this.captureplayer = captureplayer;
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxyObj, Method method, Object[] args) throws Throwable {
		log.debug("Invoking proxy for method " + method + "(" + args + ")");
		Object returnValue = invoke(new MethodInvocationImpl(target, method, args));
		log.debug("Proxy method " + method + "(" + args + ") returns "+ returnValue);
		
		return returnValue;

	}
	
    /**
     * Invoke the method.
     * 
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    protected Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = null;
        
        
        switch(mode) {
        case OFF:
            if(log.isInfoEnabled()) {
                log.info("Invoking method " + invocation.getMethod().getDeclaringClass().getName() + "#" + invocation.getMethod().getName());
            }
            result = invocation.proceed();
            break;
        case CAPTURE:
            result = invocation.proceed();
            captureplayer.capture(invocation, result);
            break;
        case REPLAY: 
            result = captureplayer.replay(invocation);
            break;
        case SMOOTH:
            try {
                result = captureplayer.replay(invocation);
            } catch (NoCapturedObjectException e) {
                if(log.isInfoEnabled()) {
                    log.info("Invoking method " + invocation.getMethod().getDeclaringClass().getName() + "#" + invocation.getMethod().getName());
                }

                result = invocation.proceed();
            }
        }
        
        return result;
    }


	public Object getTarget() {
		return target;
	}

	public CaptureplayMode getMode() {
		return mode;
	}

	public void setMode(CaptureplayMode mode) {
		if(mode==null) {
			throw new IllegalArgumentException("mode must not be null setting the CaptureplayMode property.");
		}
		this.mode = mode;
	}

	

}
