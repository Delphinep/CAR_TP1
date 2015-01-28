package FTPServer.Server;

/**
 * Class which permits to create an error message
 */
public class ErrorMessage {

	/**
	 * Number error of the object
	 */
	private int error_number;
	
	/**
	 * Message error of the object
	 */
	private String error_message;
	
	/**
	 * Constructor of the ErrorMessage object
	 * @param error_number Number error
	 * @param error_message Message error
	 */
	public ErrorMessage(int error_number, String error_message) {
		this.error_number = error_number;
		this.error_message = error_message;
	}
	
	/**
	 * Return a string which represents the entire ErrorMessage object
	 */
	public String toString() {
		return this.error_number +" : " + this.error_message;
	}
	
}
