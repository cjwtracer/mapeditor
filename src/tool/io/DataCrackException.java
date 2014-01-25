package tool.io;

/**
 * When there encouter a DataCrackException, the back up files won't be saved.
 * @author caijw
 *
 */
public class DataCrackException extends Exception {
	private static final long serialVersionUID = -2289451105387912023L;
	
	public DataCrackException(Throwable cause){
		super(cause);
	}
}
