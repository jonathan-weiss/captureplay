package ch.captureplay.proxy.impl;

import java.lang.reflect.Method;

import ch.captureplay.proxy.MethodInvocation;

/**
 * <p>Simple MethodInvocation implementation.</p>
 * @author Jonathan Weiss
 */
public class MethodInvocationImpl implements MethodInvocation{

	protected Object target;
	protected Method method;
	protected Object[] arguments;
	
	
	
	/**
	 * Constructor
	 */
	public MethodInvocationImpl(Object target, Method method,Object[] arguments) {
		this.target = target;
		this.method = method;
		this.arguments = arguments;
	}

	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.MethodInvocation#getMethod()
	 */
	public Method getMethod() {
		return method;
	}

	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.Invocation#getArguments()
	 */
	public Object[] getArguments() {
		return arguments;
	}

	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.Joinpoint#getThis()
	 */
	public Object getTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.Joinpoint#proceed()
	 */
	public Object proceed() throws Throwable {
		return getMethod().invoke(getTarget(), getArguments());
	}

}
