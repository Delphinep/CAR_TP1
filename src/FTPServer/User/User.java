package FTPServer.User;

/**
 * Class which permits to implement a FTP User
 */
public class User {

	/**
	 * Private string which define the name of the user
	 */
	private String username;
	
	/**
	 * Private string which represents the password of the user
	 */
	private String passwd;
	
	/**
	 * Constructor of the User object
	 * @param username The username of the user
	 * @param passwd The password of the user
	 */
	public User() {
		this.username = username;
		this.passwd = passwd;
	}

	public boolean checkIdentity(){
	    //TODO : in a file, assert the user exist
	    return true;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the passwd
	 */
	public String getPasswd() {
		return passwd;
	}

	/**
	 * @param passwd the passwd to set
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	/**
	 * toString method
	 */
	public String toString() {
		return "User: "+this.username;
	}
	
	/**
	 * Equals method
	 */
	public boolean equals(Object object) {
		if (object instanceof User && ((User) object).getUsername().equals(this.getUsername()) && this.getPasswd().equals(this.getPasswd()))
			return true;
		else
			return false;
	}
	
}
