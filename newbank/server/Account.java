package newbank.server;
public class Account {
	// account type either "Current Account" or "Savings Account"
	String accountType;
	String accountName;
	double currentBalance;

	public Account(String accountType, String accountName, double amount) {
		this.accountType = accountType;
		this.accountName = accountName;
		this.currentBalance = amount;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public String getAccountName() {
		return accountName;
	}

	public String getAccountType() {
		return accountType;
	}

	//withdraw funds
	public boolean withdraw(double w)
	{
		if(w<= currentBalance)
		{
			currentBalance = currentBalance -w;
			return true;
		}
		else
			return false;
	}

	public void setAmount(double amount) {
		this.currentBalance = amount;
	}
	//deposit into the account
	public void deposit(double d)
	{
		currentBalance = currentBalance +d;
	}
	//transfer from current account to the account passed.
	public void transfer(Account acc, double amount)
	{
		if(amount<=this.currentBalance)
		{
			acc.setAmount(acc.getCurrentBalance()+amount);
			this.currentBalance -=amount;
			System.out.println("Amount transferred successfully.");
		}
		else
		{
			System.out.println("Sorry, You dont have enough funds");

		}
	}

	public void setAccountName(String name) {
		this.accountName = name;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
}
