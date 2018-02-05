package ch.captureplay.strategy;

import ch.captureplay.proxy.MethodInvocation;

/**
 * 
 * Exception is thrown, if a return object of 
 * a method invocation is asked to replay, but haven't 
 * been captured yet.
 * 
 * @author Jonathan Weiss
 *
 */
@SuppressWarnings("serial")
public class NoCapturedObjectException extends RuntimeException {

	/**
     * The method invocation that didn't return a object.
     */
    protected MethodInvocation invocation; 
    
    /**
     * Constructor.
     * @param invocation The method invocation that didn't return a object.
     */
    public NoCapturedObjectException(MethodInvocation invocation) {
        super();
        this.invocation = invocation;
    }
    
    /**
     * Constructor.
     * @param invocation The method invocation that didn't return a object.
     * @param innerException The detailed exception, why the return object coudln't be found.
     */
    public NoCapturedObjectException(MethodInvocation invocation, Throwable innerException) {
        super(innerException);
        this.invocation = invocation;
    }

    /**
     * Return the method invocation that didn't return a object and 
     * led to this exception.
     * @return The method invocation that didn't return a object.
     */
    public MethodInvocation getInvocation() {
        return invocation;
    }
    
    

}
