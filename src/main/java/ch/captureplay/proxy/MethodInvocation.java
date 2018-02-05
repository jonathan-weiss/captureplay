package ch.captureplay.proxy;

import java.lang.reflect.Method;

/**
 * Description of a method call.
 * @author Jonathan Weiss
 *
 */
public interface MethodInvocation {


	/**
	 * @return The "real" object that is called.
	 */
	public abstract Object getTarget();

	/**
	 * @return The arguments, that are used with this call.
	 */
	public abstract Object[] getArguments();

	/**
	 * @return The Method on the target object.
	 */
	public abstract Method getMethod();

	/**
	 * Calls the <code>method</code> on <code>target</code> with the arguments <code>arguments</code>.
	 * @return
	 * @throws Throwable
	 */
	public abstract Object proceed() throws Throwable;

	
}
