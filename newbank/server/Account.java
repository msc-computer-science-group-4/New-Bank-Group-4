package newbank.server;
import java.util.Random;

public class Account {
	// account type either "Current Account" or "Savings Account"
	String accountType;
	String accountName;
	double currentBalance;
	String IBAN;
	boolean IBANCreated;

	public Account(String accountType, String accountName, double amount) {
		this.accountType = accountType;
		this.accountName = accountName;
		this.currentBalance = amount;
		this.IBANCreated = false;
	}

	// Credit: https://github.com/arturmkrtchyan/iban4j
	// Generates International Bank Account Numbers
	public void generateIBAN() {
		int accountNumber = 10000000;
		Random ID = new Random();
		accountNumber += ID.nextInt(90000000);
		String NewIBAN = "GB24NWBK999999" + accountNumber;
		this.IBANCreated = true;
		this.IBAN = NewIBAN;
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

	public String getIBAN() {
		// if no IBAN has been created yet, creating one
		if(this.IBANCreated == false){
			generateIBAN();
		}
		// otherwise, returning the one that has already been created
		return this.IBAN;
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

	public void setAccountName(String name) {
		this.accountName = name;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
}
