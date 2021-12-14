package newbank.server;

//importing all java utility libraries
import java.util.*;
import java.time.LocalDate;

public class NewBank {

	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private ArrayList<Loan> loans;

	private NewBank() {
		customers = new HashMap<>();
		loans = new ArrayList<>();
		addTestData();
	}
	// request: 4,1,100
	private String createNewAccount(CustomerID customer, String request) {
		List<String> input = Arrays.asList(request.split("\\s*,\\s*"));
		System.out.println(input.get(1));
		String accountType = input.get(1);
		String accountName = input.get(2);
		double amount = Double.parseDouble(input.get(3));
		Customer thisCustomer = customers.get(customer.getUserName());
		if (accountType.equals("1")) {
			accountType = "Current Account";
		}
		if (accountType.equals("2")) {
			accountType = "Savings Account";
		}
		thisCustomer.addAccount(new Account(accountType, accountName, amount));
		return "Successfully created a new " + String.valueOf(accountType) + " named '" + accountName +
				"' with an initial balance of " + String.valueOf(amount) + "£.";
	}

	public void addTestData() {
		Customer test = new Customer("test", "testuser", "Test1234#");

		test.addAccount(new Account("Current Account", "Main", 1000.0));
		test.addAccount(new Account("Savings Account", "Savings", 1500.0));
		test.addAccount(new Account("Current Account", "Checking", 250.0));

		loans.add(new Loan("loan-testuser-2021-12-05", 10000.0, 10, 0.05, "testuser"));
		loans.add(new Loan("loan-testuser-2021-12-03", 20000.0, 20, 0.09, "testuser"));

		customers.put(test.getCustomerID().getUserName(), test);
	}

	public static NewBank getBank() {
		return bank;
	}

	public HashMap<String,Customer> getCustomers(){
		return customers;
	}
	public ArrayList<Loan> getLoans(){
		return loans;
	}

	public boolean twoFactorAuthentication(int authenticationNumber) throws Exception {
		//Key provided by https://github.com/j256/two-factor-auth
		String base32Secret = "NY4A5CPJZ46LXZCP";
		boolean correctValue = TwoFactorAuthentication.validateCurrentNumber(base32Secret, authenticationNumber, TwoFactorAuthentication.DEFAULT_TIME_STEP_SECONDS*1000);
		return correctValue;
	}

	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		for (Map.Entry<String, Customer> customer: customers.entrySet()){
			String username = customer.getValue().getCustomerID().getUserName();
			String pass = customer.getValue().getCustomerID().getPassword();
			if(username.equals(userName)){
				if(pass.equals(password)){
					CustomerID customerID = customer.getValue().getCustomerID();
					return customerID;
				}
			} else {
				continue;
			}
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		//moving back to switch statements as it's more readable code
		//if-else statements are more suited for boolean values and switch statements are faster in terms of compilation
		//reference: https://www.geeksforgeeks.org/switch-vs-else/
		if(customers.containsKey(customer.getUserName())) {
			List<String> input = Arrays.asList(request.split("\\s*,\\s*"));
			Customer currentCustomer = customers.get(customer.getUserName());
			switch(input.get(0)) {
				case "1" :
				case "DISPLAYSELECTABLEACCOUNTS" :
					return showMyAccounts(customer);

				case "DISPLAYSELECTEDNAMEACCOUNT":
					return getSelectedAccountName(customer, Integer.parseInt(input.get(1)));

				case "DISPLAYSELECTEDLOANNAME":
					return getSelectedLoanName(Integer.parseInt(input.get(1)));

				case "NUMBEROFUSERACCOUNTS":
					return String.valueOf(currentCustomer.getAccounts().size());

				case "NUMBEROFOFFEREDLOANS":
					int numberOfOfferedLoans = 0;
					for (Loan loan : loans){
						if (loan.borrowerName.equals("NONE")){
							numberOfOfferedLoans += 1;
						}
					}
					return String.valueOf(numberOfOfferedLoans);

				case "4" : return createNewAccount(customer, request);

				case "SHOWLOANS" : return loansToString(input.get(1));

				case "CHECKACCOUNTBALANCE":
					return String.valueOf(currentCustomer.getAccount(input.get(1)).getCurrentBalance());

				case "CLOSEACCOUNT":
					return currentCustomer.closeAccount(currentCustomer.getAccount(input.get(1)));

				case "TRANSFERTOUSER":
					Customer receiver = customers.get(input.get(1));
					Double transferableSum = Double.parseDouble(input.get(3));
					String res = transferToUser(currentCustomer, receiver, transferableSum,input.get(4), input.get(2));
					if (res == "success") {
						return "Successfully sent " +transferableSum+ "£ from your account '" + input.get(4)
								+ "' to account " + input.get(2) + ".";
					}
					else if (res == "This IBAN does not match any of the receiver's accounts IBAN."){
						return res;
					}
					else {
						return "Something went wrong. Please, try again later.";
					}

				case "TRANSFERTOSELF":
					Account receiverAccount = currentCustomer.getAccount(input.get(1));
					Account fromAccount = currentCustomer.getAccount(input.get(2));
					Double amountToTransfer = Double.parseDouble(input.get(3));
					return transferToSelf(fromAccount, receiverAccount, amountToTransfer);

				case "ADDMONEY":
					return addMoneyToAccount(customer, input.get(1), Double.parseDouble(input.get(2)));

				case "TRANSFERANDCLOSE":
					Account closingAccount = currentCustomer.getAccount(input.get(1));
					double remainingBalance = closingAccount.getCurrentBalance();
					Account transferToAccount = currentCustomer.getAccount(input.get(2));
					return transferToSelf(closingAccount, transferToAccount, remainingBalance) + "\n" +
							currentCustomer.closeAccount(closingAccount);

				case "OFFERLOAN":
					String loanName = "loan-" + customer.getUserName() + '-' + LocalDate.now();
					Double loanAmount = Double.parseDouble(input.get(2));
					Double rate = Double.parseDouble(input.get(3));
					int loanTerm = Integer.parseInt(input.get(4));
					String response = offerLoan(customer, loanName, loanAmount, rate, loanTerm, input.get(1));
					if (response == "success") {
						return "Successfully added the loan '" + loanName + "' over " + loanAmount +
								"£ with a period of " + loanTerm + " days to the ledger of offered loans.\n";
					} else {
						return "Something went wrong. Please, try again later.";
					}

				case "CHANGELOANSTATUS":
					for (Loan loan : loans){
						if (loan.getLoanName().equals(input.get(1))){
							loan.setBorrowerName(customer.getUserName());
						}
					}
					return "success";

				case "TRANSFERLOAN":
					String fundReceivingAccount = input.get(1);
					for (Loan loan : loans){
						if (loan.getLoanName().equals(input.get(2))){
							addMoneyToAccount(customer, fundReceivingAccount, loan.getLoanAmount());
							return "The Loan amount of " + String.valueOf(loan.getLoanAmount()) +
									"£ was added to the account: '" + fundReceivingAccount + "'. \n" +
									"You have to repay the creditor the above stated Loan amount plus " +
									"interest within the next " + String.valueOf(loan.getLoanTerm()) + " days.";
						}
					}
					return "fail";

				case "WITHDRAWLOAN":
					for (Loan loan : loans){
						if (loan.getLoanName().equals(input.get(1)) && loan.getborrowerName().equals("NONE")){
							// add withheld money back to creditors account
							//Account refundAccount = currentCustomer.getAccount(input.get(2));
							addMoneyToAccount(customer, input.get(2), loan.getLoanAmount());
							//then remove loan from loan list
							loans.remove(loan);
							return "Successfully deleted your offered loan: '" + loan.getLoanName() +
									"' and added the loan amount of " + loan.getLoanAmount() + "£ back to account: '" +
									input.get(2) + "'.";
						}
					}
					return "fail";



				default : return "FAIL";

			}
		}
		return "FAIL";
	}

	public synchronized String processAccountCreationRequest(String request) throws Exception {
		List<String> input = Arrays.asList(request.split("\\s*,\\s*"));
		switch(input.get(0)) {
			case "ACCOUNTCREATION" : return createLoginAccount(request);
			default : return "FAIL";
		}
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

	private String createLoginAccount(String request) {
		List<String> input = Arrays.asList(request.split("\\s*,\\s*"));
		String customerName = input.get(1);
		String userName = input.get(2);
		String password = input.get(3);

		//Validate password
		String passwordResponse = isValidPassword(password);
		boolean validUsername = isUsernameValid(userName);
		if (passwordResponse != "TRUE"){
			return passwordResponse;
		} else if (validUsername==false) {
			String output = "The username already exists.\nPlease enter a unique username or type 'esc' to return to the menu screen.";
			return output;
		} else {
			Customer newCustomer = new Customer(customerName, userName, password);       // create new customer
			newCustomer.addAccount(new Account("Current Account", "Main", 00.0));    // create a default account for the customer
			bank.customers.put(userName, newCustomer);        // add the customer to the list of customers and assign their username
			// generate iban for new customer who has just been created and return in the output
			Account MainAccount = newCustomer.getAccount("Main");
			String iban = MainAccount.getIBAN();
			String output = "New user '" + userName + "' created.\n" +
					"We also created an initial bank account:'Main' with the IBAN: " + iban + " for you.\n";
			return output;
		}
	}

	public boolean isUsernameValid(String userName) {
		for (Map.Entry<String, Customer> customer: customers.entrySet()) {
			String username = customer.getValue().getCustomerID().getUserName();
			if (username.equals(userName)) {
				return false;
			}
		}
		return true;
	}

	//Password validation (Credit: https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java//)
	// Using Regex
	public static String isValidPassword(String password)
	{
		String isPasswordValid = "TRUE";
		if (password.length() > 15 || password.length() < 8)
		{
			return "Password has to be less than 20 and above 8 characters in length.";
		}
		String upperCaseChars = "(.*[A-Z].*)";
		if (!password.matches(upperCaseChars ))
		{
			return "Password has to have a minimum of one uppercase (capital case) letter";
		}
		String lowerCaseChars = "(.*[a-z].*)";
		if (!password.matches(lowerCaseChars ))
		{
			return "Password has to have a minimum of one lowercase letter";
		}
		String numbers = "(.*[0-9].*)";
		if (!password.matches(numbers ))
		{
			return "Password must have at a minimum of one digit number";
		}
		//regex
		String specialChars = "(.*[@,#,$,%].*$)";
		if (!password.matches(specialChars ))
		{
			return "Password must have at a minimum of one special character among @#$%";
		}
		return isPasswordValid;
	}

	Customer getIndex(String newP)
	{
		return customers.getOrDefault(newP,null);
	}

	private String showMyAccounts(CustomerID customer) {
		return customers.get(customer.getUserName()).accountsToString();
	}

	private String showAllTakenLoans() {
		return loansToString("taken");
	}

	/**
	 * This method searches for the selected account by index and returns the name of the found account
	 */
	private String getSelectedAccountName(CustomerID customer, Integer accountIndex) {
		ArrayList<Account> accounts = customers.get(customer.getUserName()).getAllAccounts();
		return accounts.get(accountIndex).getAccountName();
	}

	/**
	 * This method searches for the selected Loan by index and returns the name of the found Loan
	 */
	private String getSelectedLoanName(Integer loanIndex) {
		return loans.get(loanIndex).getLoanName();
	}


	/**
	 * This method returns table of offered (all loans that haven't been taken yet) or taken loans.
	 */
	public String loansToString(String type) {
		String loanNameHeading = "Loan Name";
		String loanAmountHeading = "Loan Amount";
		String loanTermHeading = "Loan Term";
		String loanRateHeading = "Interest Rate";
		String loanCreditorHeading = "Creditor";
		String s = ""; // the output variable of this function

		// if barrowerName is equal NONE in a loan then it's a loan that could be offered.
		// if barrowerName isn't equal NONE in a loan then it's a loan that has been taken.
		int longestLoanNameCount=loanNameHeading.length();
		ArrayList<Loan> loanList = new ArrayList<>();
		for(Loan l : loans) {
			String barrowerName = l.getborrowerName();
			if ((barrowerName.equals("NONE") && type.equals("offered")) || (!barrowerName.equals("NONE") && type.equals("taken"))) {
				loanList.add(l);
			}
		}

		for(Loan l : loanList) {
			if(l.getLoanName().length() > longestLoanNameCount) {
				longestLoanNameCount = l.getLoanName().length();
			}
		}

		int longestAmountCount=loanAmountHeading.length();
		for(int i=0; i<longestAmountCount-5; i++){
			loanAmountHeading += " ";
		}

		int longestTermCount=loanTermHeading.length();
		for(int i=0; i<longestTermCount-1; i++){
			loanTermHeading += " ";
		}

		int longestRateCount=loanRateHeading.length();
		for(int i=0; i<longestRateCount-3; i++){
			loanRateHeading += " ";
		}

		int longestCreditorCount=loanCreditorHeading.length();
		for(int i=0; i<longestCreditorCount-3; i++){
			loanCreditorHeading += " ";
		}

		s += loanNameHeading+"        "+loanAmountHeading+"        "+loanTermHeading+"        "+loanRateHeading+"        "+loanCreditorHeading+"\n";

		// Divider
		int dividerLength = s.length();
		for(int i=0; i<dividerLength; i++){
			s += "-";
		}
		s += "\n";

		// Accounts detail
		int counter = 1;
		for(Loan l : loanList) {
			s += counter + "." + l.getLoanName();
			for(int i = 0; i<longestLoanNameCount-l.getLoanName().length(); i++){
				s += " ";
			}
			s += "      " + l.getLoanAmount() + " ";
			s += "        ";
			s += l.getLoanTerm();
			s += "        ";
			s += l.getLoanInterestRate();
			s += "        ";
			s += l.getCreditorUserName();
			s += "\n";
			counter+=1;
		}
		// return output
		return s; }

	/**
	 * This method adds a new loan's offer
	 */
	private String offerLoan(CustomerID customer, String loanName, Double loanAmount, Double rate, int loanTerm, String accountName) {
		try {
			String userName =  customer.getUserName();

			//new offer adds to NewBank list - loans
			loans.add(new Loan(loanName, loanAmount, loanTerm, rate, userName));

			//after the offer is ready, the bank writes off the customers' money
			Account account = customers.get(customer.getUserName()).getAccount(accountName);
			double balance = account.getCurrentBalance();
			account.setAmount(balance-loanAmount);

			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	/**
	 * This method finds the account of the current customer and the recipient's account,
	 * after which the balances of these accounts are updated
	 */
	private String transferToUser(Customer currentCustomer, Customer receiver, Double transferableSum, String CustomerAccountName, String receiverIban) {
		try {
			Account fromAccount = currentCustomer.getAccount(CustomerAccountName);
			Double balanceFromAccount = fromAccount.getCurrentBalance();
			Account toAccount = receiver.getAccountIBAN(receiverIban);

			if (toAccount == null){
				return "This IBAN does not match any of the receiver's accounts IBAN.";
			}
			Double balanceToAccount = toAccount.getCurrentBalance();
			fromAccount.setAmount(balanceFromAccount-transferableSum);
			toAccount.setAmount(balanceToAccount+transferableSum);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	/**
	 * This method works similarly to the above method but is for transferring between accounts for the same user. This
	 * method also replaces the transfer() method that was in the Account class.
	 */
	private String transferToSelf(Account fromAccount, Account toAccount, Double transferableSum) {
		fromAccount.withdraw(transferableSum);
		toAccount.deposit(transferableSum);
		return "Successfully transferred " + String.valueOf(transferableSum) + "£ from account '" + fromAccount.getAccountName()
				+ "' to account '" + toAccount.getAccountName() + "'.";
	}

	/**
	 * This method set a new balance for a selected account.
	 */
	private String addMoneyToAccount(CustomerID customer, String accountName, double amount) {
		Account account = customers.get(customer.getUserName()).getAccount(accountName);
		double balance = account.getCurrentBalance();
		account.setAmount(balance+amount);
		return "Successfully added " + amount + "£ to account: '" + accountName + "'.";
	}
}
