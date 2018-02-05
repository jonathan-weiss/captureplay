package ch.captureplay.strategy.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import ch.captureplay.proxy.MethodInvocation;
import ch.captureplay.strategy.MethodCaptureplayer;
import ch.captureplay.strategy.NoCapturedObjectException;

/**
 * <p>Implementation of the {@link MethodCaptureplayer} interface 
 * with <code>Properties</code> and XStream.</p>
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
public class XStreamPropertiesCaptureplayer extends XStreamCaptureplayer implements MethodCaptureplayer {
	public final static String DEFAULT_FILENAME = "captuReplay.properties";
	
	protected String filepath;
	protected String filename;
	
	
	public XStreamPropertiesCaptureplayer() {
		this(DEFAULT_FILENAME);
	}
	
	public XStreamPropertiesCaptureplayer(String filename) {
		this(System.getProperty("java.io.tmpdir"), filename);
	}

	public XStreamPropertiesCaptureplayer(String filepath, String filename) {
		this.filepath = filepath;
		this.filename = filename;
	}

	protected String getPathToPropertiesFile() {
		String fileSeparator = System.getProperty("file.separator");
		return this.filepath + fileSeparator + this.filename;
	}
	protected void loadProperties(Properties props) {
    	
    	Reader reader = null;
		try {
			reader = new FileReader(getPathToPropertiesFile());
			props.load(reader);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found: " + getPathToPropertiesFile(), e);
		} catch (IOException e) {
			throw new RuntimeException("File couldn't be read: " + getPathToPropertiesFile(), e);
		}
		finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
    }
    
	private void storeProperties(Properties props) {
		Writer writer = null;
		try {
			writer = new FileWriter(getPathToPropertiesFile());
			props.store(writer , "Captureplay properties");
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found: " + getPathToPropertiesFile(), e);
		} catch (IOException e) {
			throw new RuntimeException("File couldn't be read: " + getPathToPropertiesFile(), e);
		}
		finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		
	}


    /* (non-Javadoc)
     * @see ch.captureplay.strategy.impl.OSCacheCaptureplayer#getFromMap(org.aopalliance.intercept.MethodInvocation, java.lang.String)
     */
    protected synchronized Object getFromMap(MethodInvocation invocation, String key) throws NoCapturedObjectException {
    	loadProperties(propertiesMap);
    	return super.getFromMap(invocation, key);
    }



    /* (non-Javadoc)
     * @see ch.captureplay.strategy.impl.OSCacheCaptureplayer#putInMap(org.aopalliance.intercept.MethodInvocation, java.lang.String, java.lang.Object)
     */
    protected synchronized void putInMap(MethodInvocation invocation, String key, Object object) {
    	super.putInMap(invocation, key, object);
        storeProperties(propertiesMap);
    }


   

}
