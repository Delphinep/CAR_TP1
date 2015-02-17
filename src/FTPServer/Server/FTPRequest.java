package FTPServer.Server;

/*
 * JAVA import
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import util.RootFileCSV;
/*
 * Class import
 */
import FTPServer.User.User;

/**
 * Class which permits to allow to implement a FTP request
 */
public class FTPRequest extends Thread {

	/**
	 * Socket communication (communication with client)
	 */
	private Socket socket_communication;
	
	/**
	 * Socket to send data (with client)
	 */
	private Socket socket_data;
	
	/**
	 * Stream to write data for the client
	 */
	private DataOutputStream dos;
	
	/**
	 * CSV file
	 */
	private RootFileCSV csv_database;
	
	/**
	 * User of the FTP request
	 */
	private User user;
	
	/**
	 * Path of the root repository (for users)
	 */
	private final static String repository_root_PATH = "./root_file_repository/";
	
	/**
	 * Current path of the user, in the FTP server
	 */
	private String current_path;
	
	/**
	 * Boolean to show if the session is over, or not
	 */
	private boolean finish;
	
	/**
	 * Constructor of the FTPRequest object
	 * @param socket_communication The socket communication with the client
	 * @param path The root file for users
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
            sendMessageCom(CodeMessage.CODE_220,"");
            
            /*
             * Process requests
             */
            while(!this.finish){
                String[] request;
                try{
                	/*
                	 * Split the request with " " -> to split the head and the message of the request
                	 */
                    request = br.readLine().split(" ");
                }
                catch (SocketException e){
                    /*
                     * The socket was closed. No problem need to be raised.
                     */
                    break;
                }
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
                	processRequest(request[0], request[1]);                
                else
                	processRequest(request[0], "");
            }
            
            is.close();
            
            os.close();
            
	    } catch (IOException e) {
            System.err.println("Problem encounter during the connection with the user");
            return;
	    }
	   
	}

	/**
	 * Method which allows to make general treatments for the input request
	 */
	public void processRequest(String request_head, String request_msg) {
        System.out.println(request_head +   "    " +  request_msg);

        /*
         * Switch which contains all commands to process individually
         */
		switch(request_head) {
		case "LIST":
            try {
                this.processList();
            } catch (IOException e) {
                sendMessageCom(CodeMessage.CODE_500, "Error with the data socket.");
            }
            return;
		case "PASS":
			try {
				this.processPass(request_msg);
			} 
			catch (Exception e) {
				sendMessageCom(CodeMessage.CODE_503,"");
			}
			return;
        case "PORT":
            this.processPort(request_msg);
            return;
        case "QUIT":
            try {
                processQuit();
            } catch (IOException e) {
                sendMessageCom(CodeMessage.CODE_500, "Error while closing the connection.");
            }
            return;
        case "RETR":
        	try {
				this.processRetr(request_msg);
			} catch (IOException e) {
				sendMessageCom(CodeMessage.CODE_500, "Error with files.");
	        }
        	return;
        case "STOR":
        	try {
				this.processStor(request_msg);
			} catch (IOException e) {
				sendMessageCom(CodeMessage.CODE_500, "Error with files.");
			}
        	return;
        case "SYST":
			this.processSyst();
			return;
        case "TYPE":
            this.processType(request_msg);
            return;
		case "USER":
            this.processUser(request_msg);
            return;
		}
        sendMessageCom(CodeMessage.CODE_502,"");
	}
	
	/**
	 * Method which allows to change the data representation type (ASCII or BINARY)
	 */
	public void processType() {
	    sendMessageCom(CodeMessage.CODE_200, "Type changement : OK");
	}

	/**
	 * Method which allows to process the USER command
	 * @param username The username of the actual user, to log in
	 */
	public void processUser(String message) {
	    user.setUsername(message);
	    sendMessageCom(CodeMessage.CODE_331,"");
	}

	/**
	 * Method which allows to process the PASS command
	 * @param passwd The password of the actual user
	 * @throws Exception Exception raises by the checkIdentity() method
	 */
	public void processPass(String passwd) throws Exception {
		this.user.setPasswd(passwd);
		if (this.checkIdentity())
			sendMessageCom(CodeMessage.CODE_200,"");
		else
			sendMessageCom(CodeMessage.CODE_530,"");
	}
	
	/**
	 * Method which allows to process the SYST command
	 */
	public void processSyst() {
		sendMessageCom(CodeMessage.CODE_215, "Unix system.");
	}
	
	/**
	 * Method which allows to create a new stream for the data connection
	 * @param request A string which contains the IP and port of the data connection
	 */
	public void processPort(String request) {
		String[] split_request = request.split(",");
		String ip = split_request[0]+"."+split_request[1]+"."+split_request[2]+"."+split_request[3];
		int port = 256 * Integer.parseInt(split_request[4]) + Integer.parseInt(split_request[5]);
		
		/*
		 * Data socket connection
		 */
		try {
			this.socket_data = new Socket(ip, port);
			sendMessageCom(CodeMessage.CODE_225,"");
			return;
		} catch (IOException e) {
			sendMessageCom(CodeMessage.CODE_500,"No connection for data...");
		}
        sendMessageCom(CodeMessage.CODE_500, "Illegal port command.");
		
	}

	/**
	 * Method which allows to process the RETR command
	 * @param file_name The file name of the file to retrieve
	 * @throws IOException An exception raises if the file can't be found
	 */
	public void processRetr(String request) throws IOException {
		
		File file_to_send = new File(this.current_path + request);
		
		if (!file_to_send.exists()) {
			sendMessageCom(CodeMessage.CODE_550, "File not found.");			
			return;
		}
		
		/*
         * Send the file to the user
         */
		OutputStream os = this.socket_data.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		
		/*
		 * Data transfer : OK
		 */
		sendMessageCom(CodeMessage.CODE_125, "");
		
		FileInputStream fis = new FileInputStream(file_to_send);
		byte[] buffer_socket = new byte[this.socket_data.getReceiveBufferSize()];
		int bytes_read = 0;
		
		/*
		 * Send all bytes
		 */
		while ((bytes_read = fis.read(buffer_socket)) > 0)
			dos.write(buffer_socket, 0, bytes_read);
		
		/*
		 * Close all
		 */
		fis.close();
		
		dos.flush();
		
		sendMessageCom(CodeMessage.CODE_226, "");
		
		dos.close();
		
	}
	
	/**
	 * Method which allows to process the STOR command
	 * @param file_name The file name of the file to store
	 * @throws IOException An exception raises if the file can't be found
	 */
	public void processStor(String file_name) throws IOException {
		
		File file_to_send = new File(this.current_path + file_name);
		
		/*
         * Send the file to the user
         */
		InputStream is = this.socket_data.getInputStream();
		
		/*
		 * Data transfer : OK
		 */
		sendMessageCom(CodeMessage.CODE_125, "");
		
		FileOutputStream fos = new FileOutputStream(file_to_send);
		byte[] buffer_socket = new byte[this.socket_data.getReceiveBufferSize()];
		int bytes_read = 0;
		
		/*
		 * Store all bytes
		 */
		while ((bytes_read = is.read(buffer_socket)) != -1)
			fos.write(buffer_socket, 0, bytes_read);

		/*
		 * Close all
		 */
		fos.flush();
		
		fos.close();
		
		sendMessageCom(CodeMessage.CODE_226, "");
		
		is.close();
		
	}
	
	/**
	 * Method which allows to process the LIST command
	 * @throws IOException Exception raises if the actual path file can't be found
	 */
	public void processList() throws IOException {
		File actual_file = new File(this.current_path);
		String message_to_return = "List of files into "+this.current_path+" :\r\n";
		/*
		 * For all files, if the studied file is a file -> f + getName; else d + getName
		 */
		for (File fileEntry: actual_file.listFiles()) {
			if (fileEntry.isFile())
				message_to_return += "f: "+fileEntry.getName()+"\r\n";
			if (fileEntry.isDirectory())
				message_to_return += "d: "+fileEntry.getName()+"\r\n";
		}
		
		/*
         * Send the list via the socket_data
         */
		OutputStream os = this.socket_data.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		
		sendMessageCom(CodeMessage.CODE_125, "");
		dos.writeBytes(message_to_return);
		dos.flush();
		socket_data.close();
		sendMessageCom(CodeMessage.CODE_226,"List successfully send");
	}
	
	/**
	 * Method which allows to quit the session
	 * @throws IOException Exception raises if socket_data or socket_communication can't be close
	 */
	public void processQuit() throws IOException {
	    sendMessageCom(CodeMessage.CODE_221, "");
	    this.finish = false;
	    this.socket_data.close();
		this.socket_communication.close();  
	}

	/**
	 * Method which allows to check the identity of the user
	 * @return A boolean - result of the identification of the user : True if the authentification is done, else False
	 * @throws Exception Exception returned if the csv_database can't be used
	 */
	public boolean checkIdentity() throws Exception{
		String username = this.user.getUsername();
		if (this.csv_database.checkUser(username)) {
			if (this.user.getPasswd().equals(this.csv_database.getUsersList().get(username)))
				return true;
		}
		return false;
	}
	
	/**
	 * Method which allows to send a com' message
	 * @param code The message code to send
	 * @param message The message to send
	 */
	public void sendMessageCom(CodeMessage code, String message) {
		try {
			/*
			 * If the message exists, we send to the client the message (code + message or only code)
			 */
	        if(message.length() < 1)
	            this.dos.writeBytes(new FTPMessage(code).getMessage());
	        else 
	            this.dos.writeBytes(new FTPMessage(code, message).getMessage());
	    } 
	    catch (IOException e) {
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
