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
            sendMessageCom(CodeMessage.CODE_220,"");
            
            /*
             * Process requests
             */
            while(!this.finish){
                String[] request;
                try{
                    request = br.readLine().split(" ");
                }
                catch (SocketException e){
                    //The socket was closed. No problem need to be raised.
                    
                    //End of the thread
                    return;
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
	
	public void processType(String message) {
	    sendMessageCom(CodeMessage.CODE_200, "Type changement : OK");
	}

	/**
	 * Method which allows to process the USER command
	 * @param message
	 */
	public void processUser(String message) {
	    user.setUsername(message);
	    sendMessageCom(CodeMessage.CODE_331,"");
	}

	/**
	 * Method which allows to process the PASS command
	 * @param request
	 * @throws Exception 
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
	 * @return A string with a small description of the Unix system
	 */
	public void processSyst() {
		sendMessageCom(CodeMessage.CODE_215, "Unix system.");
	}
	
	/**
	 * Method which allows to create a new stream for the data connection
	 * @param request The IP and port of the data connection
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
	public void processList() throws IOException {
		File actual_file = new File(this.current_path);
		String message_to_return = "List of files into "+this.current_path+" :\n";
		/*
		 * For all files, if the studied file is a file -> f + getName; else d + getName
		 */
		for (File fileEntry: actual_file.listFiles()) {
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
		
		sendMessageCom(CodeMessage.CODE_125, "");
		dos.writeBytes(message_to_return +"\n");
		dos.flush();
		socket_data.close();
		sendMessageCom(CodeMessage.CODE_226,"List successfully send");
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
	public void sendMessageCom(CodeMessage code, String message) {
	    try {
	        if(message.length() < 1)
	            this.dos.writeBytes(new FTPMessage(code).getMessage());
	        else 
	            this.dos.writeBytes(new FTPMessage(code, message).getMessage());
	    } 
	    catch (IOException e) {
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
