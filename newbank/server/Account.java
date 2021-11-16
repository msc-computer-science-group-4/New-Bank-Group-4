package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;
	// added a new field for current balance
	private double currentBalance;

	public Account(String accountName, double openingBalance, ) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		// added this to constructor
		this.currentBalance = openingBalance;
	}


	public double getOpeningBalance() {
		return openingBalance;
	}
  
  
  //withdraw funds
	public boolean withdraw(double w)
	{
		if(w<=openingBalance)
		{
			openingBalance=openingBalance-w;
			return true;
		}
		else
			return false;
	}

  public void setAmount(double amount) {
		this.openingBalance = amount;
	}
  
  	//deposit into the account
   /**
	 * method to deposit into the account
	 * @author Mahtab Nejad
	 */
	public void deposit(double d)
	{
		openingBalance=openingBalance+d;
	}
  
   /**
	 * method to transfer from current account to the account passed.
	 * @author Mahtab Nejad
	 */
	public void transfer(Account acc, double amount)
	{
		if(amount<=this.openingBalance)
		{
			acc.setAmount(acc.getOpeningBalance()+amount);
			this.openingBalance-=amount;
			System.out.println("Amount transferred successfully.");
		}
		else
		{
			System.out.println("Sorry, You dont have enough funds");

		}
	}
  
  	/**
	 * Set method to set the Account name.
	 * @author Mahtab Nejad
	 */
  	public void setAccountName(String name) {
		this.accountName = name;
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
