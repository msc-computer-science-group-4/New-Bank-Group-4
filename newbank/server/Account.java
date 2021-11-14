
public class Account {
	String accountName;
	double openingBalance;

	public Account(String username, double amount) {
		this.accountName = username;
		this.openingBalance = amount;
	}

	public String getAccountName() {
		return accountName;
	}

	public double getOpeningBalance() {
		return openingBalance;
	}

	//withdraw money
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
	public void deposit(double d)
	{
		openingBalance=openingBalance+d;
	}
	//transfer from current account to the account passed.
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

	public void setAccountName(String name) {
		this.accountName = name;
	}
}
