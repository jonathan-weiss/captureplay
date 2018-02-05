package ch.captureplay.strategy.impl;

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.captureplay.proxy.MethodInvocation;
import ch.captureplay.strategy.MethodCaptureplayer;
import ch.captureplay.strategy.NoCapturedObjectException;

/**
 * Abstract implementation for map based MethodCaptureplayer implementation.
 * 
 * @author Jonathan Weiss
 *
 */
public abstract class AbstractMapCaptureplayer implements MethodCaptureplayer {

    private static Log log = LogFactory.getLog(AbstractMapCaptureplayer.class);
    
    public static final String DEFAULT_CACHE_NAME = "captureplayer";
    
    private String objectDiscriminator;
    private String methodDiscriminator;
    private String argumentDiscriminator;
    private String argumentTypeDiscriminator;
    private String collectionTypeDiscriminator;
    private static final String DEFAULT_METHOD_DISCRIMINATOR = "&";
    private static final String DEFAULT_OBJECT_DISCRIMINATOR = "@";
    private static final String DEFAULT_ARGUMENT_DISCRIMINATOR = "-";
    private static final String DEFAULT_ARGUMENT_TYPE_DISCRIMINATOR = "#";
    private static final String DEFAULT_COLLECTION_TYPE_DISCRIMINATOR = ";";
    
    

    public AbstractMapCaptureplayer() {
        super();
        this.methodDiscriminator = DEFAULT_METHOD_DISCRIMINATOR;
        this.objectDiscriminator = DEFAULT_OBJECT_DISCRIMINATOR;
        this.argumentDiscriminator = DEFAULT_ARGUMENT_DISCRIMINATOR;
        this.argumentTypeDiscriminator = DEFAULT_ARGUMENT_TYPE_DISCRIMINATOR;
        this.collectionTypeDiscriminator = DEFAULT_COLLECTION_TYPE_DISCRIMINATOR;
    }
    
    /**
     * Stores a value as a map structure.
     * @param invocation All information about the method signature, 
     * its argument and return type and the argument parameter objects.
     * @param key The map key to store the object under.
     * @param object The map value to store.
     */
    protected abstract void putInMap(MethodInvocation invocation, String key, Object object);
    
    /**
     * Receives a value out of the map structure.
     * @param invocation All information about the method signature, 
     * its argument and return type and the argument parameter objects.
     * @param key The map key the object was stored under.
     * @return The object that was stored under
     * @throws NoCapturedObjectException If the key wasn't found.
     */
    protected abstract Object getFromMap(MethodInvocation invocation, String key) throws NoCapturedObjectException;
    
    /**
     * Returns the count of keys, stored in the map structure.
     * @return The count of cached values
     */
    public abstract int getMapSize();
    

    /* (non-Javadoc)
     * @see MethodCaptureplayer#capture(org.aopalliance.intercept.MethodInvocation, java.lang.Object)
     */
    public void capture(MethodInvocation invocation, Object returnedObject) throws Throwable {
        String key = createMapKey(invocation);
        if(log.isDebugEnabled()) {
            log.debug("Map Key: " + key);
        }
        putInMap(invocation, key, returnedObject);
    }
    


    /* (non-Javadoc)
     * @see MethodCaptureplayer#replay(org.aopalliance.intercept.MethodInvocation)
     */
    public Object replay(MethodInvocation invocation) throws NoCapturedObjectException {
        String key = createMapKey(invocation);
        if(log.isDebugEnabled()) {
            log.debug("Map key: " + key);
        }
        Object result = getFromMap(invocation, key);

        if(log.isDebugEnabled()) {
            log.debug("Returning data for key [" + key + "]:" + result);
        }
        return result;

    }
    

    /**
     * Returns dependant of the invocation the name of the map.
     * If you always wants to use the same cache, you can use 
     * this method.
     */
    protected String getMapName(MethodInvocation invocation) {
        return DEFAULT_CACHE_NAME;
    }
    

    
    /**
     * Construct a map key put without vm addresses.
     * @see Idee of org.springframework.aop.interceptor.cache.CacheInterceptor#getCacheKey(java.lang.Object, java.lang.Object[], java.lang.Class[])
     */
    protected String createMapKey(MethodInvocation invocation) {
        StringBuffer result = new StringBuffer();
        result.append(invocation.getMethod().getDeclaringClass().getName());
        result.append(methodDiscriminator);
        result.append(invocation.getMethod().getName());

        Object[] arguments = invocation.getArguments();
        Class<?>[] argumentClasses = invocation.getMethod().getParameterTypes();
        
        if (arguments != null) {
            result.append(objectDiscriminator);
            for (int i = 0; i < arguments.length; i++) {
                if (i > 0) {
                    result.append(argumentDiscriminator);
                }
                result.append(argumentClasses[i].getName());
                result.append(argumentTypeDiscriminator);
                //Hier kann ein komplexes Objekt dran sein, 
                //wir m�ssen es �ber mehrere JVM eindeutig 
                //identifizieren k�nnen.
                //-> Rekursiver Aufbau der Id
                result.append(createUniqueIdentifier(arguments[i]));
                //Urspr�nglich: getKey(arguments[i])
            }
        }

        return result.toString();
    }
    
    /**
     * Creates a unique name for this object, based on its 
     * primitive variables in it. But the key tries not to 
     * have VM addresses in it, so it doesn't take any toString() 
     * results, but only from primitive objects. 
     * @param parameter The object to create a unique key.
     * @return A unique identifier for this object.
     */
    protected String createUniqueIdentifier(Object parameter) {
        StringBuffer identifier = new StringBuffer();
        if(parameter == null) {
            identifier.append("");
        }
        else if(parameter instanceof Object[]) {
            StringBuffer subIdentifier = new StringBuffer();
            Object array[] = (Object[]) parameter;
            for (int i = 0; i < array.length; i++) {
                Object element = array[i];
                if (i > 0) {
                    subIdentifier.append(collectionTypeDiscriminator);
                }
                subIdentifier.append(createUniqueIdentifier(element));
            }
            subIdentifier.insert(0,"[");
            subIdentifier.append("]");
            identifier.append(subIdentifier.toString());
        }
        else if(parameter instanceof Collection<?>) {
            StringBuffer subIdentifier = new StringBuffer();
            Collection<?> collection = (Collection<?>) parameter;
            for (Object element : collection) {
                if (subIdentifier.length() > 0) {
                    subIdentifier.append(collectionTypeDiscriminator);
                }
                subIdentifier.append(createUniqueIdentifier(element));
            }
            subIdentifier.insert(0,"[");
            subIdentifier.append("]");
            identifier.append(subIdentifier.toString());
        }
        else if(
                parameter.getClass().isAssignableFrom(Number.class) ||
                parameter.getClass().isAssignableFrom(String.class) ||
                parameter.getClass().isAssignableFrom(Boolean.class)
                ) {
            identifier.append(parameter.toString());
        }
        else {
            StringBuffer subIdentifier = new StringBuffer();
            Field[] fields = parameter.getClass().getFields();
            //Method[] methods = parameter.getClass().get;
            for (Field field : fields) {
                if (field.isAccessible()) {
                    String fieldName = field.getName();
                    String fieldValue;
                    try {
                        fieldName = field.getName();
                        if (field.getType().isPrimitive()) {
                            fieldValue = field.get(parameter).toString();
                        } else {
                            fieldValue = createUniqueIdentifier(field.get(parameter));
                        }
                    } catch (IllegalAccessException e) {
                        // shouldn't occure, because we 
                        // check the accessibility explicitly
                        throw new RuntimeException(e);
                    }
                    if(subIdentifier.length() > 0) {
                        subIdentifier.append(",");
                    }
                    subIdentifier.append(fieldName);
                    subIdentifier.append("(");
                    subIdentifier.append(fieldValue);
                    subIdentifier.append(")");
                }
            }
            if(subIdentifier.length() > 0) {
                identifier.append(subIdentifier.toString());
            }
            else {
                /*
                 * We can not extract a primitive value. To 
                 * not call the toString method, that whould 
                 * maybe return a vm memory address, we get 
                 * the hash value of the object.
                 */
                identifier.append(parameter.hashCode());
            }
            
        }
        
        //return identifier.toString();
        String result = new Integer(identifier.toString().hashCode()).toString();
        return result;
    }

}
