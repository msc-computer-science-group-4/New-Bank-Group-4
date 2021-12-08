package newbank.server;


import java.util.ArrayList;

public class Customer {

	private ArrayList<Account> accounts;
	private ArrayList<Loan> loans;
	private CustomerID customerID;
	private String Name;

	public Customer(String customerName, String userName, String password) {
		accounts = new ArrayList<>();

		loans = new ArrayList<>();
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
	public ArrayList<Loan> getLoans() {
		return loans;
	}


	// Return the list of accounts of a customer as a string
	public String accountsToString() {
		String accountNameHeading = "Account Name";
		String accountTypeHeading = "Account Type";
		String currentBalanceHeading = "Current Balance";
		String IBANHeading = "IBAN: ";
		String s = ""; // the output variable of this function

		int longestAccountNameCount=accountNameHeading.length();
		for(Account a : accounts) {
			if(a.getAccountName().length() > longestAccountNameCount) {
				longestAccountNameCount = a.getAccountName().length();
			}
		}

		int longestAccountTypeCount=accountTypeHeading.length();
		for(Account a : accounts) {
			if(a.getAccountType().length() > longestAccountTypeCount) {
				longestAccountTypeCount = a.getAccountType().length();
			}
		}

		// Header
		if (accountNameHeading.length() < longestAccountNameCount) {
			int difference = longestAccountNameCount-accountNameHeading.length();
			for(int i=0; i<difference; i++){
				accountNameHeading += " ";
			}
		}

		if (accountTypeHeading.length() < longestAccountTypeCount) {
			int difference = longestAccountTypeCount-accountTypeHeading.length();
			for(int i=0; i<difference; i++){
				accountTypeHeading += " ";
			}
		}


		s += accountNameHeading+"        "+accountTypeHeading+"        "+currentBalanceHeading+"        "+IBANHeading+"\n";

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
			for(int i = 0; i<longestAccountNameCount-a.getAccountName().length(); i++){
				s += " ";
			}
			s += "      " + a.getAccountType() + " ";
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

	//public void addOfferedLoan(Loan offeredLoan) { offeredLoans.add(offeredLoan); }

	public Account getAccount(String accountName) {
		for(Account a : accounts){
			if (a.getAccountName().equals(accountName)) {
				return a;
			}
		}
		return null;
	}

	public Account getAccountIBAN(String iban) {
		for(Account a : accounts){
			if (a.getIBAN().equals(iban)) {
				return a;
			}
		}
		return null;
	}

	public ArrayList<Account> getAllAccounts() {
		return accounts;
	}

	// remove Account
	public String closeAccount(Account account) {
		String accountType = account.getAccountType();
		String accountName = account.getAccountName();
		accounts.remove(account);
		return "Successfully closed the " + accountType + " '" + accountName + "'.";
	}

	// get number of accounts
	public int numAccounts() {
		return accounts.size();
	}

}