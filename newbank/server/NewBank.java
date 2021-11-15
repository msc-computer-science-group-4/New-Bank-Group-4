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
			// added current customer variable to top of method instead of declaring it for each command
			Customer currentCustomer = customers.get(customer.getKey());


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

				/* account names should not be allowed to have a blank symbol in them. This is because it leads
				to problems when splitting the string in the MOVE command (It won't be clear if a word belongs to the
				 fromAccount or the toAccount*/
				if(accountName.contains(" ")){
					return "Account names are not allowed to contain blank symbols.";
				}

				// guard against too short or too long account name.
				if (accountName.length() < 2 || accountName.length() > 20){
					return "Please retry enter between 2 and 20 characters for your account name.";
				}
				// create a new account with an opening balance of 0.
				currentCustomer.addAccount(new Account(accountName, 0));

				// return success message containing new account name and balance.
				return "Successfully opened the account " + "\"" + accountName + "\"" +
						" with an initial balance of 0$.";

			}



			else if (request.startsWith("CLOSEACCOUNT ")) {
				//Same logic as in the NEWACCOUNT request handling
				String closingName = request.split(" ", 2)[1];

				/* If an account with the closingName exists, attempt to close it. closeAccount() does the checks and
				returns a String message */
				for(Account account : currentCustomer.getAccounts()){
					if(account.getAccountName().equals(closingName)){
						return currentCustomer.closeAccount(account);
					}
				}
				return "There exists no account " + "\"" + closingName + "\"" + " under your name.";
			}



			else if (request.startsWith("MOVE ")) {
				/* Splits e.g., the command "MOVE 1000.25 Main Savings" into 4 pieces where index 1 is the transfer
				amount, index 2 the fromAccount name and index 3 the toAccount name (index 0 is the MOVE command). */
				String[] moveRequest = request.split(" ", 4);

				// amount to be transferred
				double amount;
				// accounts to transfer from/to
				Account fromAccount = null;
				Account toAccount = null;


				// try to parse the stated amount as a double, if it can't be parsed inform user about invalid amount
				try{
					amount = Double.parseDouble(moveRequest[1]);
				}
				catch(NumberFormatException e){
					return "Please enter a valid numerical amount to transfer.";
				}

				/* go through every account under the users name to get the Account objects matching the requested
				account names */
				for(Account account : currentCustomer.getAccounts()){
					if(account.getAccountName().equals(moveRequest[2])){
						fromAccount = account;
					}
					if(account.getAccountName().equals(moveRequest[3])){
						toAccount = account;
					}
				}

				/* If the users entered an invalid fromAccount or invalid toAccount , or both are invalid, then let the
				user know which account name is invalid. */
				if(fromAccount == null && toAccount == null){
					return "There exists no account " + "\"" + moveRequest[2] + "\"" + " under your name.\n"+
							"There exists no account " + "\"" + moveRequest[3] + "\"" + " under your name.";
				}
				if(fromAccount == null){
					return "There exists no account " + "\"" + moveRequest[2] + "\"" + " under your name.";
				}
				if(toAccount == null){
					return "There exists no account " + "\"" + moveRequest[3] + "\"" + " under your name.";
				}

				// now verify if the fromAccount has a sufficient balance to transfer the amount
				if(fromAccount.getCurrentBalance() < amount){
					return "Your account \"" + fromAccount.getAccountName() + "\" has an insufficient balance.";
				}

				// if balance is sufficient, initiate transfer (which returns Success message).
				return currentCustomer.transfer(amount, fromAccount, toAccount);

			}
		}

		return "FAIL";
	}

	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}
