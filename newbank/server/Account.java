package newbank.server;
import java.util.Random;
public class Account {
	String accountName;
	double currentBalance;
	String iban;
	boolean ibanCreated;

	public Account(String account, double amount) {
		this.accountName = account;
		this.currentBalance = amount;
		this.ibanCreated = false;
	}

	// Credit: https://github.com/arturmkrtchyan/iban4j
	// Generates International Bank Account Numbers
	public String generateIBAN() {
		int accountNumber = 10000000;
		Random ID = new Random();
		accountNumber += ID.nextInt(90000000);
		String IBAN = "GB24NWBK999999" + accountNumber;
		return IBAN;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public String getIBAN() {
		// if no IBAN has been created yet, creating one
		if(this.ibanCreated == false){
			generateIBAN();
		}
		// otherwise, returning the one that has already been created
		return iban;
	}

	public String getAccountName() {
		return accountName;
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
}
