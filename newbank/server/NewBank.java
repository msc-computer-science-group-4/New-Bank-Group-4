package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			/* I replaced the switch cases with if statements to verify certain conditions for the input String,
			   which I think is easier with if statements.*/
			if (request.equals("SHOWMYACCOUNTS")) {
				return showMyAccounts(customer);
			}

			// NEWACCOUNT request: First verifies if the request starts with NEWACCOUNT followed by a blank symbol.
			else if (request.startsWith("NEWACCOUNT ")) {
				/* splits the user input into a list at the first occurrence of a blank symbol.
				Everything after the blank symbol is part of the account name. */
				String accountName = request.split(" ", 2)[1];

				// guard against too short or too long account name.
				if (accountName.length() < 2 || accountName.length() > 20){
					return "Please retry enter between 2 and 20 characters for your account name.";
				}
				// retrieve Customer object for current customer and create a new account with an opening balance of 0.
				Customer currentCustomer = customers.get(customer.getKey());
				currentCustomer.addAccount(new Account(accountName, 0));

				// return success message containing new account name and balance.
				return "Successfully opened the account " + "\"" + accountName + "\"" + " with an initial balance 0f 0.";

			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}
