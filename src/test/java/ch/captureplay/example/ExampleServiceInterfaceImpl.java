package ch.captureplay.example;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Implementation of the {@link ExampleServiceInterface}.
 * @author Jonathan Weiss
 *
 */
public class ExampleServiceInterfaceImpl implements ExampleServiceInterface {

	private int addition = 4;
	
	public ExampleServiceInterfaceImpl(int addition) {
		this.addition = addition;
	}
	
	public ExampleServiceInterfaceImpl() {}




	/* (non-Javadoc)
	 * @see ch.captureplay.example.ExampleServiceInterface#returnWithAddition(int)
	 */
	public int returnWithAddition(int inputInt) {
		return inputInt + addition;
	}
	
	

    /* (non-Javadoc)
     * @see ch.captureplay.example.ExampleServiceInterface#getSimpleList(java.lang.String)
     */
    public List<Object> getSimpleList(String argument) {
        List<Object> list = new ArrayList<Object>(1);
        list.add(argument);
        
        return list;
    }


    /* (non-Javadoc)
     * @see ch.captureplay.example.ExampleServiceInterface#getSimpleString(java.lang.String)
     */
    public String getSimpleString(String argument) {
        return argument;
    }
    
    

    /* (non-Javadoc)
     * @see ch.captureplay.example.ExampleServiceInterface#getObjByTwoArgs(java.lang.Integer, java.lang.Object)
     */
    public Object getObjByTwoArgs(Integer number, Object object) {
        return  object.toString() +"/" + number.toString();
    }


    /* (non-Javadoc)
     * @see ch.captureplay.example.ExampleServiceInterface#getSimpleObject(java.lang.Object)
     */
    public Object getSimpleObject(Object argument) {
        return argument;
    }

	public int getAddition() {
		return addition;
	}

	public void setAddition(int addition) {
		this.addition = addition;
	}
	

}
