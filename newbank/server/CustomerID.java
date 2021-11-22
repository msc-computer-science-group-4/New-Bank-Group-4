package newbank.server;

public class CustomerID {
	private CustomerID customerID;
	private String Name;
	private String userName;
	private String password;
	private String IBAN;

	public CustomerID(String name, String userName, String password, String iban) {
		this.Name = name; this.userName = userName; this.password = password; this.IBAN = iban;
	}

	public String getName() {
		return Name;
	}

	public String getIBAN()
	{
		return IBAN;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

}
