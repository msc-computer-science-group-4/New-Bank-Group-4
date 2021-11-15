package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;
	// added a new field for current balance
	private double currentBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		// added this to constructor
		this.currentBalance = openingBalance;
	}


	/**
	 * Getter for the Account name.
	 * @author Lorenz
	 * @return String the Account name
	 */
	public String getAccountName(){
		return accountName;
	}


	/**
	 * Getter for the Account name.
	 * @author Lorenz
	 * @return double the current account balance
	 */
	public double getCurrentBalance(){
		return currentBalance;
	}


	/**
	 * Setter method to adjust the current account balance. As of now there are no overdraft limits, so there are no
	 * checks in place here. Once we add overdraft to accounts, we can introduce checks for negative balances etc.
	 * @author Lorenz
	 * @param amount the amount to be ADDED to the current balance. Use a negative param to subtract from the current
	 *               balance.
	 */
	public void adjustBalance(double amount){
		currentBalance += amount;
	}


	public String toString() {
		/* made a small change here from openingBalance to currentBalance so that it shows the correct balance after
		using the MOVE command */
		return (accountName + ": " + currentBalance);
	}

}
