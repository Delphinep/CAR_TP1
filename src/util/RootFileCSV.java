package util;

/*
 * JAVA import
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;

/**
 * Class for the CSV file
 */
public class RootFileCSV {

	/**
	 * List of users - each contains the username and the password
	 */
	private HashMap<String, String> users_list;
	
	/**
	 * Pathname of the CSV file
	 */
	private static final String pathfile = "./accounts.csv";
	
	/**
	 * Constructor of the object
	 */
	public RootFileCSV() {
		this.users_list = new HashMap<String, String>();
		this.readFile();
	}
	
	/**
	 * Method to read the CSV file
	 */
	public void readFile() {
		
		String line;
		
		try {
			BufferedReader file = new BufferedReader(new FileReader(RootFileCSV.getPathfile()));
			while ((line = file.readLine()) != null) {
				String[] user = line.split(";");
				this.users_list.put(user[0], user[1]);
			}
			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR : File "+RootFileCSV.pathfile+" not found...");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR : I/O Exception for "+RootFileCSV.pathfile);
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to check if the username is contains on the users list
	 * @param username
	 * @return
	 */
	public boolean checkUser(String username) {
		return this.users_list.containsKey(username);
	}
	
	/**
	 * Method to return the password of a user, done by his username
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public String getPasswd(String username) throws Exception {
		if (this.checkUser(username))
			return this.users_list.get(username);
		else
			throw new Exception("ERROR: "+username+" is not saved in the database...");
	}

	/**
	 * @return the users_list
	 */
	public HashMap<String, String> getUsersList() {
		return users_list;
	}

	/**
	 * @param users_list the users_list to set
	 */
	public void setUsersList(HashMap<String, String> users_list) {
		this.users_list = users_list;
	}

	/**
	 * @return the pathfile
	 */
	public static String getPathfile() {
		return pathfile;
	}
	
}
