package FTPServer.Server;

/*
 * JAVA import
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Class import
 */
import util.File;

/**
 * Class which allows to implement a multi-thread FTP server
 */
public class FTPServer {
	
	public static ArrayList<FTPRequest> ftp_requests = new ArrayList<FTPRequest>();
	private static ServerSocket socket;

	public static void main(String[] args) {
		
	    File csv_database = new File();
	    
		try {
			
			FTPServer.socket = new ServerSocket(4000);
			
			while(true) {
				
				Socket socket_communication = socket.accept();
				FTPRequest request = new FTPRequest(socket_communication, csv_database);
				FTPServer.addFtpRequest(request);
				request.run();
				
			}
			
		} 
		catch (IOException e) {
			System.out.println("ERROR: "+e.getMessage());
		}
		
	}

	/**
	 * @return the ftp_requests
	 */
	public static ArrayList<FTPRequest> getFtpRequests() {
		return ftp_requests;
	}

	/**
	 * @param ftp_requests the ftp_requests to set
	 */
	public static void setFtpRequests(ArrayList<FTPRequest> new_ftp_requests) {
		ftp_requests = new_ftp_requests;
	}
	
	/**
	 * Method to add a FTP request to the ftp_requests attribute
	 * @param ftp_request
	 */
	public static void addFtpRequest(FTPRequest ftp_request) {
		ftp_requests.add(ftp_request);
	}

}
