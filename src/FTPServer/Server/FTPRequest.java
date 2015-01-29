package FTPServer.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import FTPServer.Server.ErrorMessage;

import FTPServer.User.User;

/**
 * Class which permits to allow to implement a FTP request
 */
public class FTPRequest extends Thread {

	Socket socket_communication;
	User user;
	boolean finish;
	
	public FTPRequest(Socket socket_communication, String path) {
		this.user = new User();
		finish = false;
	}
	/**
	 * Method to run a FTP request
	 */
	public void run() {
	    
	    try {
            InputStream is = socket_communication.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            while(!finish){
                
                String[] request = br.readLine().split(" ");
                processRequest(request[0], request[1]);                
                
                
                
            }
            
            
	    
	    
	    
	    } catch (IOException e) {
            System.err.println("Problem encounter during the connection with the user");
            e.printStackTrace();
            return;
        }
	    
	    

	
	}

	/**
	 * Method which allows to make general treatments for the input request
	 */
	public void processRequest(String request_head, String request_msg) {
		switch(request_head) {
		case "USER":
			this.processUser(request_msg);
			break;
		case "PASS":
			this.processPass(request_msg);
			break;
		}
	}

	public void processUser(String message) {
	    user.setUsername(message);
	}

	
	public void processPass(String request) {
		this.user.setPasswd(request);
		this.user.checkIdentity();

	}

	public void processRetr() {

	}
	
	public void processStor() {

	}
	
	public void processList() {

	}
	
	public void processQuit() throws IOException {
		this.socket_communication.close();
		finish = false;
	}

}
