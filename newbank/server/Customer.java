package newbank.server;


import java.util.ArrayList;

public class Customer {

	private ArrayList<Account> accounts;
	private CustomerID customerID;
	private String Name;

	public Customer(String customerName, String userName, String password) {
		accounts = new ArrayList<>();
		customerID = new CustomerID(customerName, userName, password);
		Name = customerID.getName();
	}

	public CustomerID getCustomerID() {
		return customerID;
	}


	public String getName() {
		return Name;
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}


	// Return the list of accounts of a customer as a string
	public String accountsToString() {
		String accountNameHeading = "Account Name";
		String currentBalanceHeading = "Current Balance";
		String IBANHeading = "Account IBAN";
		String s = ""; // the output variable of this function

		int longestAccountNameCount=accountNameHeading.length();
		for(Account a : accounts) {
			if(a.getAccountName().length() > longestAccountNameCount) {
				longestAccountNameCount = a.getAccountName().length();
			}
		}

		// Header
		if (accountNameHeading.length() < longestAccountNameCount) {
			int difference = longestAccountNameCount-accountNameHeading.length();
			for(int i=0; i<difference; i++){
				accountNameHeading += " ";
			}
		}
		s += accountNameHeading+"        "+currentBalanceHeading+"        "+IBANHeading+"\n";

		// Divider
		int dividerLength = s.length();
		for(int i=0; i<dividerLength; i++){
			s += "-";
		}
		s += "\n";

		// Accounts detail
		int counter = 1;
		for(Account a : accounts) {
			s += counter + "." + a.getAccountName();
			for(int i=0;i<longestAccountNameCount-a.getAccountName().length();i++){
				s += " ";
			}
			s += "        ";
			s += a.getCurrentBalance();
			s += "        ";
			s += a.getIBAN();
			s += "\n";
			counter+=1;
		}

		// return output
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);
	}

	public Account getAccount(String accountName) {
		for(Account a : accounts){
			if (a.getAccountName().equals(accountName)) {
				return a;
			}
		}
		return null;
	}

	public ArrayList<Account> getAllAccounts() {
		return accounts;
	}

	// remove Account
	public void closeAccount(Account account) {
		accounts.remove(account);
	}

	// get number of accounts
	public int numAccounts() {
		return accounts.size();
	}

}
