package ch.captureplay.strategy;

import ch.captureplay.proxy.MethodInvocation;

/**
 * <p>Interface for a capture/replay implementation.</p>
 * <p>An implementation is able to persist the data of a 
 * method call ({@link #capture(MethodInvocation, Object)}) 
 * and return the stored data without calling the method again
 * ({@link #replay(MethodInvocation)})</p> 
 * 
 * 
 * @author Jonathan Weiss
 *
 */
public interface MethodCaptureplayer {
    
    /**
     * Persist the return object of a method, so it can be called later 
     * by the replay method.
     * @param invocation All information about the method signature, 
     * its argument and return type and the argument parameter objects.
     * @param returnedObject The object that the method did return.
     * @throws Throwable if an error occurs.
     */
    public void capture(MethodInvocation invocation, Object returnedObject) throws Throwable;
    
    /**
     * Get a former captured object that was persisted and returns it.
     * 
     * @param invocation All information about the method signature, 
     * its argument and return type and the argument parameter objects.
     * @return The object that was captured with this <code>MethodInvocation</code>.
     * @throws NoCapturedObjectException if no object was captured before.
     * 
     */
    public Object replay(MethodInvocation invocation) throws NoCapturedObjectException;

}
