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
	public String getMessage() {

	    switch(code){
	    case CODE_125 :
	        return "125 : Data connection already open; transfer starting. " + info + "\n";
	    case CODE_200 :
	        return "200 : action successfully completed. " + info + "\n";
	    case CODE_215 :
	        return "" + info + "\n";
	    case CODE_220 : 
	        return "220 : Service ready. " + info  + "\n";
	    case CODE_221 : 
	        return "221 : Service closing control connection. " + info + "\n";
	    case CODE_225 :
	        return "225 : Data connection open; no transfer in progress. " + info + "\n";
	    case CODE_226 : 
	        return "226 : Closing data connection. " + info + "\n";
	    case CODE_331 :
	        return "331 : User name okay, need password. " + info + "\n";
	    case CODE_500 :
	        return "500 : The command was not accepted and the requested action did not take place. " + info  + "\n";
	    case CODE_502 :
	        return "502 : command not implemented. " + info + "\n";
	    case CODE_503 :
	        return "503 : Bad sequence of commands. " + info  + "\n";
	    case CODE_530 :
	        return "530 : Not logged in. " + info + "\n";
	    case CODE_550 :
	    	return "550 : File unavailable. " + info + "\n";
	    default :
	        return "502 : command not implemented.\n";
	    }
	}
}
