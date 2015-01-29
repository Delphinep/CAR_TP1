package FTPServer.Server;

/*
 * JAVA import
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
             * BufferedReader
             */
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            while(!this.finish){
                
                String[] request = br.readLine().split(" ");
                /*
                 * request[0] -> COMMAND NAME
                 * request[1] -> MSG
                 */
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
			try {
				this.processPass(request_msg);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
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
