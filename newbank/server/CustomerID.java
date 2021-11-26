package newbank.server;

public class CustomerID {
	private CustomerID customerID;
	private String Name;
	private String userName;
	private String password;
	private String IBAN;

	public CustomerID(String name, String userName, String password) {
		this.Name = name; this.userName = userName; this.password = password;
	}

	public String getName() {
		return Name;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

}
