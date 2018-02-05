package ch.captureplay.strategy.impl;

import java.util.Properties;

import ch.captureplay.proxy.MethodInvocation;
import ch.captureplay.strategy.MethodCaptureplayer;
import ch.captureplay.strategy.NoCapturedObjectException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * <p>Implementation of the {@link MethodCaptureplayer} interface 
 * with XStream.</p>
 * 
 * <p>XStream transforms an object into an xml text/string. This text/string 
 * is stored in the cache, so the recovery is always possible because a string 
 * is always serializable.</p>
 * 
 * @see <a href="http://xstream.codehaus.org/">X Stream</a> 
 * 
 * @author Jonathan Weiss
 *
 */
public class XStreamCaptureplayer extends AbstractMapCaptureplayer implements MethodCaptureplayer {
	
    protected XStream xstream = new XStream(new DomDriver()); // does not require XPP3 library
    protected Properties propertiesMap = new Properties();


    /* (non-Javadoc)
     * @see ch.captureplay.strategy.impl.OSCacheCaptureplayer#getFromMap(org.aopalliance.intercept.MethodInvocation, java.lang.String)
     */
    protected synchronized Object getFromMap(MethodInvocation invocation, String key) throws NoCapturedObjectException {
        Object result = null;
    	String xStreamResult = propertiesMap.getProperty(key);
    	if(xStreamResult == null) {
    		throw new NoCapturedObjectException(invocation);
    	}
        result = xstream.fromXML(xStreamResult);
        return result;
    }


    /* (non-Javadoc)
     * @see ch.captureplay.strategy.impl.OSCacheCaptureplayer#putInMap(org.aopalliance.intercept.MethodInvocation, java.lang.String, java.lang.Object)
     */
    protected synchronized void putInMap(MethodInvocation invocation, String key, Object object) {
        String xStreamResult = xstream.toXML(object);
        propertiesMap.put(key, xStreamResult);
    }


	@Override
	public int getMapSize() {
		return propertiesMap.size();
	}

   

}
