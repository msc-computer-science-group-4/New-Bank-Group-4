package newbank.server;
public class Loan {
	// account type either "Current Account" or "Savings Account"
	String loanName;
	double loanAmount;
	double payoffAmount;
	double interestRate;
	int loanTerm;
	String borrowerName;
	String creditorUserName;

	public Loan(String loanName, double loanAmount, int loanTerm, double interestRate, String creditorUserName) {
		this.loanName = loanName;
		this.loanAmount = loanAmount;
		this.loanTerm = loanTerm;
		this.interestRate = interestRate;
		this.borrowerName = "NONE";
		this.creditorUserName = creditorUserName;
		// initiative payoff amount to be the same as the initial Loan amount
		this.payoffAmount = loanAmount;
	}

	public double getLoanAmount() {
		return loanAmount;
	}

	public String getCreditorUserName() {return creditorUserName; }

	public String getLoanName() {
		return loanName;
	}

	public int getLoanTerm() {
		return loanTerm;
	}

	public double getLoanInterestRate() {
		return interestRate;
	}

	public double getPayoffAmount() { return payoffAmount; }

	//add interest over a certain time. Each interest amount is compounded after a number of seconds passed
	public void addInterest()
	{
		for(int i=0; i<this.loanTerm; i++) {
			this.payoffAmount = (this.payoffAmount * interestRate) + this.payoffAmount;
		}
	}

	public void makePayment(double amount) {
		this.payoffAmount = this.payoffAmount - amount;
	}

}
