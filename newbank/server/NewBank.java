package newbank.server;

//importing all java utility libraries
import java.util.*;

public class NewBank {

	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;

	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}

	private String createNewAccount(CustomerID customer, String request) {
		List<String> input = Arrays.asList(request.split("\\s*,\\s*"));
		System.out.println(input.get(1));
		String accountType = input.get(1);
		double amount = Double.valueOf(input.get(2));
		Customer thisCustomer = customers.get(customer.getUserName());
		if (accountType.equals("1")) {
			accountType = "Current Account";
		}
		if (accountType.equals("2")) {
			accountType = "Savings Account";
		}
		thisCustomer.addAccount(new Account(accountType, amount));
		return "Account '" + accountType + "' Created.\n";
	}

	public void addTestData() {
		/*
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);

		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);

		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);*/

		Customer test = new Customer("test", "testuser", "Test1234#");
		test.addAccount(new Account("Main", 1000.0));
		test.addAccount(new Account("Savings", 1500.0));
		test.addAccount(new Account("Checking", 250.0));
		customers.put(test.getCustomerID().getUserName(), test);
	}

	public static NewBank getBank() {
		return bank;
	}

	public HashMap<String,Customer> getCustomers(){
		return customers;
	}

	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		for (Map.Entry<String, Customer> customer: customers.entrySet()){
			String username = customer.getValue().getCustomerID().getUserName();
			String pass = customer.getValue().getCustomerID().getPassword();
			if(username.equals(userName)){
				if(pass.equals(password)){
					CustomerID customerID = customer.getValue().getCustomerID();
					return customerID;
				}
			} else {
				continue;
			}
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		//moving back to switch statements as it's more readable code
		//if-else statements are more suited for boolean values and switch statements are faster in terms of compilation
		//reference: https://www.geeksforgeeks.org/switch-vs-else/
		if(customers.containsKey(customer.getUserName())) {
			List<String> input = Arrays.asList(request.split("\\s*,\\s*"));
			switch(input.get(0)) {
				case "1" :
				case "DISPLAYSELECTABLEACCOUNTS" :
					return showMyAccounts(customer);
				case "2" : return createNewAccount(customer, request);
				default : return "FAIL";

			}
		}
		return "FAIL";
	}

	public synchronized String processAccountCreationRequest(String request) throws Exception {
		List<String> input = Arrays.asList(request.split("\\s*,\\s*"));
		switch(input.get(0)) {
			case "ACCOUNTCREATION" : return createLoginAccount(request);
			default : return "FAIL";
		}
	}

	private String createLoginAccount(String request) {
		List<String> input = Arrays.asList(request.split("\\s*,\\s*"));
		String customerName = input.get(1);
		String userName = input.get(2);
		String password = input.get(3);

		//Validate password
		String passwordResponse = isValidPassword(password);
		boolean validUsername = isUsernameValid(userName);
		if (passwordResponse != "TRUE"){
			return passwordResponse;
		} else if (validUsername==false) {
			String output = "The username already exists.\nPlease enter a unique username or type 'esc' to return to the menu screen.";
			return output;
		} else {
			Customer newCustomer = new Customer(customerName, userName, password);       // create new customer
			newCustomer.addAccount(new Account("Main", 00.0));    // create a default account for the customer
			bank.customers.put(userName, newCustomer);        // add the customer to the list of customers and assign their username
			Account newAccount = newCustomer.getAccount("Main");
			String newIBAN = newAccount.getIBAN();
			String output = "New user '" + userName + "' created.\n" +
					"We also created an initial bank account:'Main' with the IBAN: " + newIBAN + " for you.\n" +
					"Please Download the Google Authenticator App and use the key NY4A5CPJZ46LXZCP to set up your 2FA.";
			return output;
		}
	}

	public boolean isUsernameValid(String userName) {
		for (Map.Entry<String, Customer> customer: customers.entrySet()) {
			String username = customer.getValue().getCustomerID().getUserName();
			if (username.equals(userName)) {
				return false;
			}
		}
		return true;
	}

	//Password validation (Credit: https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java//)
	// Using Regex
	public static String isValidPassword(String password)
	{
		String isPasswordValid = "TRUE";
		if (password.length() > 15 || password.length() < 8)
		{
			return "Password has to be less than 20 and above 8 characters in length.";
		}
		String upperCaseChars = "(.*[A-Z].*)";
		if (!password.matches(upperCaseChars ))
		{
			return "Password has to have a minimum of one uppercase (capital case) letter";
		}
		String lowerCaseChars = "(.*[a-z].*)";
		if (!password.matches(lowerCaseChars ))
		{
			return "Password has to have a minimum of one lowercase letter";
		}
		String numbers = "(.*[0-9].*)";
		if (!password.matches(numbers ))
		{
			return "Password must have at a minimum of one digit number";
		}
		//regex
		String specialChars = "(.*[@,#,$,%].*$)";
		if (!password.matches(specialChars ))
		{
			return "Password must have at a minimum of one special character among @#$%";
		}
		return isPasswordValid;
	}

	Customer getIndex(String newP)
	{
		return customers.getOrDefault(newP,null);
	}

	private String showMyAccounts(CustomerID customer) {
		return customers.get(customer.getUserName()).accountsToString();
	}

}
