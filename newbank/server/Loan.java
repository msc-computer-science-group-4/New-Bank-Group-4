package newbank.server;

import java.time.LocalDate;

public class Loan {
	// account type either "Current Account" or "Savings Account"
	String loanName;
	double principalAmount;
	double initialPayoffAmount;
	double payoffAmount;
	double monthlyInterestRate;
	double totalInterestRate;
	int loanTerm;
	LocalDate startingDate;
	LocalDate deadline;
	String borrowerName;
	String creditorUserName;
	String creditorAccountName;

	public Loan(String loanName, double principalAmount, int loanTerm, double monthlyInterestRate, String creditorUserName, String creditorAccountName) {
		this.loanName = loanName;
		this.principalAmount = principalAmount;
		this.loanTerm = loanTerm;
		this.monthlyInterestRate = monthlyInterestRate;
		this.totalInterestRate = (monthlyInterestRate / 30) * loanTerm;
		this.borrowerName = "NONE";
		this.creditorUserName = creditorUserName;

		// remaining payoff amount. Here I use a temp variable to make sure the payoff amount has only 2 decimal places. Otherwise it is hard to pay off the loan entirely (there will be too many decimal places)
		double precisePayoffAmount= principalAmount + totalInterestRate * principalAmount;
		int twoDecimalPointsAmount = (int)(precisePayoffAmount*100.0);
		this.payoffAmount = ((double)twoDecimalPointsAmount)/100.0;

		// initial total remaining amount is equal to the entire payoff amount (payoffAmount will decrease but initialPayoffAmount will remain the same later)
		this.initialPayoffAmount = payoffAmount;
		this.creditorAccountName = creditorAccountName;
		this.startingDate = LocalDate.now();
	}

	public double getPrincipalAmount() {
		return principalAmount;
	}

	public String getCreditorUserName() {return creditorUserName; }

	public String getLoanName() {
		return loanName;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public String getCreditorAccountName(){
		return creditorAccountName;
	}

	public LocalDate getStartingDate(){
		return startingDate;
	}

	public LocalDate getDeadline(){
		return startingDate.plusDays(getLoanTerm());
	}

	public int getLoanTerm() {
		return loanTerm;
	}

	public double getMonthlyInterestRate() {
		return monthlyInterestRate;
	}

	public double getTotalInterestRate(){
		return totalInterestRate;
	}

	public double getPayoffAmount() { return payoffAmount; }

	public double getInitialPayoffAmount() { return initialPayoffAmount;}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}


	public void adjustPayableAmount(double amount) {
		this.payoffAmount = this.payoffAmount - amount;
	}

}
