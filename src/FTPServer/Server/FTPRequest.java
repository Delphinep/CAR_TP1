package FTPServer.Server;

/*
 * JAVA import
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import util.RootFileCSV;
/*
 * Class import
 */
import FTPServer.User.User;

/**
 * Class which permits to allow to implement a FTP request
 */
public class FTPRequest extends Thread {

	private Socket socket_communication;
	private Socket socket_data;
	private DataOutputStream dos;
	private RootFileCSV csv_database;
	private User user;
	private final static String repository_root_PATH = "./root_file_repository/";
	private String current_path;
	private boolean finish;
	
	/**
	 * Constructor of the FTPRequest object
	 * @param socket_communication
	 * @param path
	 */
	public FTPRequest(Socket socket_communication, RootFileCSV file) {
		this.socket_communication = socket_communication;
		this.csv_database = file;
		this.user = new User();
		this.current_path = repository_root_PATH;
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
            this.dos = new DataOutputStream(os);
            
            /*
             * Message : OK!
             */
            this.dos.writeBytes(new FTPMessage(220, "Service ready.\n").toString());
            
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
                 * Two methods:
                 * 		-requests have length > 1 -> send the cmd name and the message
                 * 		-requests have length = 1 -> send only the cmd (SYST for example)
                 */
                if (request.length > 1)
                	this.dos.writeBytes(processRequest(request[0], request[1]));                
                else
                	this.dos.writeBytes(processRequest(request[0], ""));
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
        System.out.println(request_head +   "    " +  request_msg);

		switch(request_head) {
		case "LIST":
            
            try {
                return this.processList();
            } catch (IOException e) {
                return new FTPMessage(500, "Error with the data socket.\n").toString();
            }
		case "PASS":
			try {
				return this.processPass(request_msg);
			} 
			catch (Exception e) {
				e.printStackTrace();
				return new FTPMessage(503, "Bad sequence of commands.\n").toString();
			}
        case "PORT":
            return this.processPort(request_msg);
        case "QUIT":
            try {
                processQuit();
            } catch (IOException e) {
                return new FTPMessage(500, "Error while closing the connection.\n").toString();
            }
            return "END";
        case "SYST":
			return this.processSyst();
        case "TYPE":
            return this.processType(request_msg);
		case "USER":
            return this.processUser(request_msg);
		}
        return new FTPMessage(502, "Syntax error, command unrecognized.\n").toString();
	}
	
	public String processType(String message) {
	    return new FTPMessage(200, "Type changement : OK\n").toString();
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
	 * Method which allows to process the SYST command
	 * @return A string with a small description of the Unix system
	 */
	public String processSyst() {
		return new FTPMessage(215, "Unix system.\n").toString();
	}
	
	/**
	 * Method which allows to create a new stream for the data connection
	 * @param request The IP and port of the data connection
	 */
	public String processPort(String request) {
		String[] split_request = request.split(",");
		String ip = split_request[0]+"."+split_request[1]+"."+split_request[2]+"."+split_request[3];
		int port = 256 * Integer.parseInt(split_request[4]) + Integer.parseInt(split_request[5]);
		
		/*
		 * Data socket connection
		 */
		try {
			this.socket_data = new Socket(ip, port);
			return new FTPMessage(225, "Command Successful\n").toString();
		} catch (IOException e) {
			System.out.println("No connection for data...");
			e.printStackTrace();
		}
        return new FTPMessage(500, "Illegal port command.\n").toString();
		
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
	 * @return The list of files and directories into the current path
	 * @throws IOException
	 */
	public String processList() throws IOException {
		File actual_file = new File(this.current_path);
		String message_to_return = "List of files into "+this.current_path+" :\n";
		/*
		 * For all files, if the studied file is a file -> f + getName; else d + getName
		 */
		for (final File fileEntry: actual_file.listFiles()) {
			if (fileEntry.isFile())
				message_to_return += "f: "+fileEntry.getName()+"\n";
			if (fileEntry.isDirectory())
				message_to_return += "d: "+fileEntry.getName()+"\n";
		}
		
		/*
         * Send the list via the socket_data
         */
		OutputStream os = this.socket_data.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		this.sendMessageCom(125, "Ready to send data\r\n");
		dos.writeBytes(message_to_return +"\r\n");
		dos.flush();
		socket_data.close();
		return new FTPMessage(226,"List successfully send\n").toString();
	}
	
	/**
	 * Method which allows to quit the session
	 * @throws IOException
	 */
	public void processQuit() throws IOException {
	    this.finish = false;
		this.socket_communication.close();
		this.socket_data.close();
	}

	/**
	 * Method which allows to check the identity of the user
	 * @return
	 * @throws Exception 
	 */
	public boolean checkIdentity() throws Exception{
		String username = this.user.getUsername();
		if (this.csv_database.checkUser(username)) {
			if (this.user.getPasswd().equals(this.csv_database.getUsersList().get(username)))
				return true;
		}
		return false;
	}
	
	/*
	 * Method which allows to send a com' message
	 */
	public void sendMessageCom(int number, String message) {
	    try {
            this.dos.writeBytes(new FTPMessage(number, message).toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	/**
	 * Method to return a string which is the representation of the session
	 */
	public String toString() {
		return "Session initialized by "+this.user.getUsername();
	}

}
