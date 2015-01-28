package FTPServer.Server;

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
	public void processRequest() {

	}

	public void processUser() {

	}

	public void processPass() {

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
