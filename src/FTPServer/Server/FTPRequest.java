package FTPServer.Server;

import java.io.IOException;
import java.net.Socket;

/**
 * Class which permits to allow to implement a FTP request
 */
public class FTPRequest extends Thread {

	Socket socket_communication;
	
	public FTPRequest(Socket socket_communication) {
		
	}
	/**
	 * Method to run a FTP request
	 */
	public void run() {

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

	public void processUser() {

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
	}

}
