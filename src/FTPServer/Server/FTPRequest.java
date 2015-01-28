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
	public void processRequest(String request) {
		String[] request_tab = request.split(request);
		String cmd = request_tab[0];
		switch(cmd) {
		case "USER":
			processUser(request_tab[1]);
			break;
		case "PASS":
			processPass(request_tab[1]);
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
