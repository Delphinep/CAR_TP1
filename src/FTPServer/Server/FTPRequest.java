package FTPServer.Server;

/*
 * JAVA import
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import util.File;
/*
 * Class import
 */
import FTPServer.User.User;

/**
 * Class which permits to allow to implement a FTP request
 */
public class FTPRequest extends Thread {

	Socket socket_communication;
	File csv_database;
	User user;
	boolean finish;
	
	/**
	 * Constructor of the FTPRequest object
	 * @param socket_communication
	 * @param path
	 */
	public FTPRequest(Socket socket_communication, File file) {
		this.socket_communication = socket_communication;
		this.csv_database = file;
		this.user = new User();
		this.finish = false;
	}
	
	/**
	 * Method to run a FTP request
	 */
	public void run() {
	    
	    try {
	    	
	    	/*
	    	 * Input Stream
	    	 */
            InputStream is = this.socket_communication.getInputStream();
            
            /*
             * Output Stream
             */
            OutputStream os = this.socket_communication.getOutputStream();
            
            /*
             * BufferedReader
             */
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            /*
             * DataOutputStream
             */
            DataOutputStream dos = new DataOutputStream(os);
            
            /*
             * Message : OK!
             */
            dos.writeBytes(new FTPMessage(220, "Service ready.\n").toString());
            
            /*
             * Process requests
             */
            while(!this.finish){
                
                String[] request = br.readLine().split(" ");
                /*
                 * request[0] -> COMMAND NAME
                 * request[1] -> MSG
                 */
                
                /*
                 * Send the result of the command
                 */
                dos.writeBytes(processRequest(request[0], request[1]));                
                
            }
            
            is.close();
            
            os.close();
            
	    } catch (IOException e) {
            System.err.println("Problem encounter during the connection with the user");
            e.printStackTrace();
            return;
        }
	   
	}

	/**
	 * Method which allows to make general treatments for the input request
	 */
	public String processRequest(String request_head, String request_msg) {
		switch(request_head) {
		case "USER":
			return this.processUser(request_msg);
		case "PASS":
			try {
				return this.processPass(request_msg);
			} 
			catch (Exception e) {
				e.printStackTrace();
				return new FTPMessage(503, "Bad sequence of commands.\n").toString();
			}
		}
		return new FTPMessage(500, "Syntax error, command unrecognized.\n").toString();
	}

	/**
	 * Method which allows to process the USER command
	 * @param message
	 */
	public String processUser(String message) {
	    user.setUsername(message);
	    return new FTPMessage(331, "User name okay, need password.\n").toString();
	}

	/**
	 * Method which allows to process the PASS command
	 * @param request
	 * @throws Exception 
	 */
	public String processPass(String passwd) throws Exception {
		this.user.setPasswd(passwd);
		if (this.checkIdentity())
			return new FTPMessage(200, "Command okay.\n").toString();
		else
			return new FTPMessage(530, "Not logged in.\n").toString();
	}

	/**
	 * Method which allows to process the RETR command
	 * @param request
	 */
	public void processRetr(String request) {

	}
	
	/**
	 * Method which allows to process the STOR command
	 * @param request
	 */
	public void processStor(String request) {

	}
	
	/**
	 * Method which allows to process the LIST command
	 * @param request
	 */
	public void processList(String request) {

	}
	
	/**
	 * Method which allows to quit the session
	 * @throws IOException
	 */
	public void processQuit() throws IOException {
		this.socket_communication.close();
		finish = false;
	}

	/**
	 * Method which allows to check the identity of the user
	 * @return
	 * @throws Exception 
	 */
	public boolean checkIdentity() throws Exception{
		String username = this.user.getUsername();
		if (this.csv_database.checkUser(username)) {
			if (this.csv_database.getPasswd(username) == this.csv_database.getUsersList().get(username))
				return true;
		}
		return false;
	}
	
	/**
	 * Method to return a string which is the representation of the session
	 */
	public String toString() {
		return "Session initialized by "+this.user.getUsername();
	}

}
