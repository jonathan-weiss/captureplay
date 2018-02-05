package ch.captureplay.example;

import java.util.List;

/**
 *  
 * @author Jonathan Weiss
 */
public interface ExampleServiceInterface {

	/**
	 * Returns an input integer added with a certain number.
	 */
	public int returnWithAddition(int inputInt);
	
    /**
     * Returns a simple array list with the argument 
     * as first and only entry.
     */
    public List<Object> getSimpleList(String argument);
    /**
     * Returns a new string but with the same name as passed in <code>argument</code>.
     */
    public String getSimpleString(String argument);
    /**
     * Returns the passed <code>argument</code> back.
     */
    public Object getSimpleObject(Object argument);
    /**
     * Returns a string with the integer, a slash and the object 
     * as toString represenation.
     * <pre>
     *   object.toString() +"/" + number.toString()
     * </pre>
     */
    public Object getObjByTwoArgs(Integer number, Object object);
    

}
