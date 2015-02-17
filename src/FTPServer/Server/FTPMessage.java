package FTPServer.Server;

/**
 * Class which permits to create an FTP message
 */
public class FTPMessage {
    
    /**
     * Ftp message code
     */
    private CodeMessage code;
	
    /**
     * Informations about the message the system want to add
     */
    private String info;
	
	/**
	 * Constructor of the FTPMessage object
	 * @param ftp_number Number of the FTP message
	 * @param ftp_message Message
	 */
	public FTPMessage(CodeMessage ftp_number) {
		this.code = ftp_number;
		this.info = "";
	}
	public FTPMessage(CodeMessage ftp_number, String details){
	       this.code = ftp_number;
	       this.info = "Details : " + details;
	}
	
	/**
	 * Return a string which represents the entire FTPMessage object
	 */
	public String toString() {
		return this.ftp_number +" : " + this.ftp_message;
	}
	
}
