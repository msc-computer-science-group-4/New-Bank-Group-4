package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	
	public Customer() {
		accounts = new ArrayList<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString() + " ";
		}
		return s;
	}

	/**
	 * I added this method so that the NewBank class can iterate over the accounts under a users name
	 * @author Lorenz
	 * @return the list of accounts under the users name
	 */
	public ArrayList<Account> getAccounts(){
		return accounts;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}


	/**
	 * This method attempts to close an existing account. Only accounts with a balance of exactly 0$ can be closed.
	 * @author Lorenz
	 * @param account the account to be closed for the current Customer object
	 * @return String the return message to the user. Informs user about the success/failure of the closing attempt.
	 */
	public String closeAccount(Account account){
		if (account.getCurrentBalance() < 0){
			return "This account currently has a negative balance. Please adjust the negative balance before" +
					" closing the account.";
		}

		else if(account.getCurrentBalance() == 0){
			accounts.remove(account);
			return "Your account had a remaining balance of 0$ and was successfully closed.";
		}

		else if (account.getCurrentBalance() > 0){
			return "This account currently has a positive balance of " + account.getCurrentBalance() +
					"$. You need to move all your funds to a different account before you can close an account." +
					"\nPlease type \"MOVE " + account.getCurrentBalance() + " " + account.getAccountName() +
					" <destination_account>\" to move your funds and then retry closing your account.";
		}
		return "";
	}


	/**
	 * This method conducts the transfer of funds between a Customers accounts. Transfers between the same account are
	 * not allowed. The checking for validity of the accounts and their funds is done in the processRequest() method
	 * in the NewBank class, NOT in here. This method will only execute if all checks were passed.
	 * @author Lorenz
	 * @param amount the amount to be transferred
	 * @param fromAccount the originator account
	 * @param toAccount the destination account
	 * @return String the return message informing the user if the transfer was successful
	 */
	public String transfer(double amount, Account fromAccount, Account toAccount){
		if(fromAccount == toAccount){
			return "You cannot transfer from an account to itself. Please choose a different destination account.";
		}

		fromAccount.adjustBalance(-amount);
		toAccount.adjustBalance(amount);
		return "You have successfully transferred " + amount + "$ from \"" + fromAccount.getAccountName() + "\" to " +
				"\"" + toAccount.getAccountName() + "\".";
	}
}
