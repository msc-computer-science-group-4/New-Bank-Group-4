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
		// testuser
		Customer test = new Customer("test", "testuser", "Test1234#");
		test.addAccount(new Account("Current Account", "Main", 1000.0));
		test.addAccount(new Account("Savings Account", "Savings", 1500.0));
		test.addAccount(new Account("Current Account", "Checking", 250.0));
		loans.add(new Loan("loan-testuser-2021-12-05", 10000.0, 10, 0.05, "testuser", "Main"));
		loans.add(new Loan("loan-testuser-2021-12-03", 20000.0, 20, 0.09, "testuser", "Savings"));

		// spareuser
		Customer spare = new Customer("spare user", "spareuser", "Spare@4567");
		spare.addAccount(new Account("Current Account", "Main", 4500.0));
		spare.addAccount(new Account("Savings Account", "Savings", 12350.0));
		spare.addAccount(new Account("Current Account", "Investing", 4800.0));
		loans.add(new Loan("loan-spareuser-2021-12-14", 2100.0, 34, 0.032, "spareuser", "Main"));
		loans.add(new Loan("loan-spareuser-2021-12-09", 7200.0, 18, 0.07, "spareuser", "Savings"));
		loans.add(new Loan("loan-spareuser-2021-12-13", 250.0, 8, 0.04, "spareuser", "Savings"));

		customers.put(test.getCustomerID().getUserName(), test);
		customers.put(spare.getCustomerID().getUserName(), spare);

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


				case "USERHASPOSTEDLOANS":
					int usersLoans = 0;
					for(Loan loan : loans){
						if(loan.getCreditorUserName().equals(input.get(1))){
							usersLoans += 1;
						}
					}
					if(usersLoans != 0){
						return "TRUE";
					}
					return "FALSE";

				case "USERHASREPAYABLELOANS":
					int repayableLoans = 0;
					for(Loan loan : loans){
						if(loan.getBorrowerName().equals(input.get(1))){
							repayableLoans += 1;
						}
					}
					if(repayableLoans != 0){
						return "TRUE";
					}
					return "FALSE";


				case "DISPLAYSELECTEDNAMEACCOUNT":
					return getSelectedAccountName(customer, Integer.parseInt(input.get(1)));

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

				case "NUMBEROFAVAILABLELOANS":
					String currentUsername = input.get(1);
					int numberOfAvailableLoans = 0;
					for (Loan loan : loans){
						if (!loan.getCreditorUserName().equals(currentUsername) && !loan.getBorrowerName().equals(currentUsername)){
							numberOfAvailableLoans += 1;
						}
					}
					return String.valueOf(numberOfAvailableLoans);

				case "NUMBEROFUSERTAKENLOANS":
					String borrowerName = input.get(1);
					int numberOfUserTakenLoans = 0;
					for (Loan loan : loans){
						if (loan.borrowerName.equals(borrowerName)){
							numberOfUserTakenLoans += 1;
						}
					}
					return String.valueOf(numberOfUserTakenLoans);

				case "NUMBEROFUSEROFFEREDLOANS":
					String creditorName = input.get(1);
					int numberOfUserOfferedLoans = 0;
					for (Loan loan : loans){
						if (loan.getCreditorUserName().equals(creditorName)){
							numberOfUserOfferedLoans += 1;
						}
					}
					return String.valueOf(numberOfUserOfferedLoans);

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
							addMoneyToAccount(customer, fundReceivingAccount, loan.getPrincipalAmount());
							return "The Loan amount of " + loan.getPrincipalAmount() +
									"£ was added to the account: '" + fundReceivingAccount + "'. \n" +
									"You have to repay the creditor a total amount of " +
									String.format("%.2f", loan.getInitialPayoffAmount()) + "£ by " + loan.getDeadline() + ".";
						}
					}
					return "fail";

				case "WITHDRAWLOAN":
					for (Loan loan : loans){
						if (loan.getLoanName().equals(input.get(1)) && loan.getBorrowerName().equals("NONE")){
							// add withheld money back to creditors account
							//Account refundAccount = currentCustomer.getAccount(input.get(2));
							addMoneyToAccount(customer, input.get(2), loan.getPrincipalAmount());
							//then remove loan from loan list
							loans.remove(loan);
							return "Successfully deleted your offered loan: '" + loan.getLoanName() +
									"' and added the loan amount of " + loan.getPrincipalAmount() + "£ to the account: '" +
									input.get(2) + "'.";
						}
					}
					return "fail";

				case "PAYABLEAMOUNT":
					return String.valueOf(Objects.requireNonNull(getCurrentLoan(input.get(1))).getPayoffAmount());

				case "SHOWCURRENTUSERTAKENLOANS":
					// show only the current customers taken loans
					return customerTakenLoansToString(customer);

				case "SHOWCURRENTUSEROFFEREDLOANS":
					// show only the current customers offered loans
					return customerOfferedLoansToString(customer);

				case "SHOWAVAILABLELOANS":
					// show all loans that the current user can take out
					return availableLoansToString(customer);

				case "REPAYLOAN":
					Loan loanToRepay = Objects.requireNonNull(getCurrentLoan(input.get(1)));
					Double repayAmount = Double.parseDouble(input.get(2));
					Customer creditor = customers.get(loanToRepay.getCreditorUserName());
					String borrowerAccountName = input.get(3);
					String creditorAccountName = loanToRepay.getCreditorAccountName();

					String loanResponse = repayLoan(currentCustomer, creditor, repayAmount, borrowerAccountName, creditorAccountName, loanToRepay);

					if (loanResponse.equals("success")) {
						return "\nSuccessfully transferred " +repayAmount+ "£ from your account '" + borrowerAccountName
								+ "' to the creditors NewBank account.\n" + "The remaining amount to repay until the " +
								loanToRepay.getDeadline() + " is " + loanToRepay.getPayoffAmount() + "£.";
					}
					else if(loanResponse.equals("paid off")){
						return "\nYou have successfully paid off the entire loan amount! This loan will now be deleted.";
					}
					return "\nSomething went wrong. Please, try again later.";

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
	 * This method returns table of offered (all loans that haven't been taken yet) or taken loans.
	 */
	public String loansToString(String type) {
		String loanNameHeading = "Loan Name";
		String loanAmountHeading = "Loan Amount";
		String loanTermHeading = "Total Loan Term (days)";
		String loanRateHeading = "Monthly Interest Rate";
		String totalLoanRateHeading = "Total Interest Rate";
		String payableAmountHeading = "Amount to Repay";
		String loanCreditorHeading = "Creditor Username";
		String s = ""; // the output variable of this function

		// if barrowerName is equal NONE in a loan then it's a loan that could be offered.
		// if barrowerName isn't equal NONE in a loan then it's a loan that has been taken.
		int longestLoanNameCount=loanNameHeading.length();
		ArrayList<Loan> loanList = new ArrayList<>();
		for(Loan l : loans) {
			String barrowerName = l.getBorrowerName();
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

		int longestTotalRateCount=totalLoanRateHeading.length();
		for(int i=0; i<longestTotalRateCount-3; i++){
			totalLoanRateHeading += " ";
		}

		int payableAmountCount=payableAmountHeading.length();
		for(int i=0; i<payableAmountCount-3; i++){
			payableAmountHeading += " ";
		}

		int longestCreditorCount=loanCreditorHeading.length();
		for(int i=0; i<longestCreditorCount-3; i++){
			loanCreditorHeading += " ";
		}

		s += loanNameHeading+"        "+loanAmountHeading+"        "+loanTermHeading+"        "+loanRateHeading+"        "+
				totalLoanRateHeading+"        "+payableAmountHeading+"        "+ loanCreditorHeading+"\n";

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
			s += "      " + l.getPrincipalAmount() + "£  ";
			s += "        ";
			s += l.getLoanTerm();
			s += "        ";
			s += l.getMonthlyInterestRate();
			s += "        ";
			s += String.format("%.4f", l.getTotalInterestRate());
			s += "        ";
			s += String.format("%.2f", l.getPayoffAmount()) + "£  ";
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
			loans.add(new Loan(loanName, loanAmount, loanTerm, rate, userName, accountName));

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
			Account toAccount = receiver.getAccountIBAN(receiverIban);
			if (toAccount == null){
				return "This IBAN does not match any of the receiver's accounts IBAN.";
			}

			fromAccount.withdraw(transferableSum);
			toAccount.deposit(transferableSum);
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
	private String repayLoan(Customer borrower, Customer creditor, Double payoffAmount, String borrowerAccountName, String creditorAccountName, Loan loan) {
		try {
			Account borrowerAccount = borrower.getAccount(borrowerAccountName);
			Account creditorAccount = creditor.getAccount(creditorAccountName);
			borrowerAccount.withdraw(payoffAmount);
			creditorAccount.deposit(payoffAmount);

			// after adjusting borrowers and creditor account balance, check and update the remaining payabale amount
			loan.adjustPayableAmount(payoffAmount);

			if(loan.getPayoffAmount() == 0){
				// if nothing is left to pay off remove loan from loan list
				loans.remove(loan);
				return "paid off";
			}
			else {
				return "success";
			}
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


	/**
	 * This method returns table of taken loans by the current user only.
	 */
	public String customerTakenLoansToString(CustomerID customer) {
		String loanNameHeading = "Loan Name";
		String loanAmountHeading = "Loan Amount";
		String loanTermHeading = "Total Loan Term (days)";
		String loanRateHeading = "Monthly Interest Rate";
		String totalLoanRateHeading = "Total Interest Rate";
		String payableAmountHeading = "Amount to Repay";
		String loanCreditorHeading = "Creditor Username";
		String s = ""; // the output variable of this function

		// if barrowerName isn't equal NONE in a loan then it's a loan that has been taken.
		int longestLoanNameCount=loanNameHeading.length();
		ArrayList<Loan> loanList = new ArrayList<>();
		for(Loan l : loans) {
			// Only show loan item for current user
			if ((l.getBorrowerName().equals(customer.getUserName()))) {
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

		int longestTotalRateCount=totalLoanRateHeading.length();
		for(int i=0; i<longestTotalRateCount-3; i++){
			totalLoanRateHeading += " ";
		}

		int payableAmountCount=payableAmountHeading.length();
		for(int i=0; i<payableAmountCount-3; i++){
			payableAmountHeading += " ";
		}

		int longestCreditorCount=loanCreditorHeading.length();
		for(int i=0; i<longestCreditorCount-3; i++){
			loanCreditorHeading += " ";
		}

		s += loanNameHeading+"        "+loanAmountHeading+"        "+loanTermHeading+"        "+loanRateHeading+"        "+
				totalLoanRateHeading+"        "+payableAmountHeading+"        "+loanCreditorHeading+"\n";

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
			s += "      " + l.getPrincipalAmount() + "£  ";
			s += "        ";
			s += l.getLoanTerm();
			s += "        ";
			s += l.getMonthlyInterestRate();
			s += "        ";
			s += String.format("%.4f", l.getTotalInterestRate());
			s += "        ";
			s += String.format("%.2f", l.getPayoffAmount()) + "£  ";
			s += "        ";
			s += l.getCreditorUserName();
			s += "\n";
			counter+=1;
		}
		// return output
		return s; }


	/**
	 * This method returns table of offered loans by the current user only
	 */
	public String customerOfferedLoansToString(CustomerID customer) {
		String loanNameHeading = "Loan Name";
		String loanAmountHeading = "Loan Amount";
		String loanTermHeading = "Total Loan Term (days)";
		String loanRateHeading = "Monthly Interest Rate";
		String totalLoanRateHeading = "Total Interest Rate";
		String payableAmountHeading = "Amount to Repay";
		String loanCreditorHeading = "Creditor Username";
		String s = ""; // the output variable of this function

		// if barrowerName isn't equal NONE in a loan then it's a loan that has been taken.
		int longestLoanNameCount=loanNameHeading.length();
		ArrayList<Loan> loanList = new ArrayList<>();
		for(Loan l : loans) {
			// Only show loan item for current user
			if ((l.getCreditorUserName().equals(customer.getUserName()))) {
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

		int longestTotalRateCount=totalLoanRateHeading.length();
		for(int i=0; i<longestTotalRateCount-3; i++){
			totalLoanRateHeading += " ";
		}

		int payableAmountCount=payableAmountHeading.length();
		for(int i=0; i<payableAmountCount-3; i++){
			payableAmountHeading += " ";
		}

		int longestCreditorCount=loanCreditorHeading.length();
		for(int i=0; i<longestCreditorCount-3; i++){
			loanCreditorHeading += " ";
		}

		s += loanNameHeading+"        "+loanAmountHeading+"        "+loanTermHeading+"        "+loanRateHeading+"        "+
				totalLoanRateHeading+"        "+payableAmountHeading+"        "+loanCreditorHeading+"\n";

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
			s += "      " + l.getPrincipalAmount() + "£  ";
			s += "        ";
			s += l.getLoanTerm();
			s += "        ";
			s += l.getMonthlyInterestRate();
			s += "        ";
			s += String.format("%.4f", l.getTotalInterestRate());
			s += "        ";
			s += String.format("%.2f", l.getPayoffAmount()) + "£  ";
			s += "        ";
			s += l.getCreditorUserName();
			s += "\n";
			counter+=1;
		}
		// return output
		return s; }


	/**
	 * This method returns table of loans that the current user can take out
	 */
	public String availableLoansToString(CustomerID customer) {
		String loanNameHeading = "Loan Name";
		String loanAmountHeading = "Loan Amount";
		String loanTermHeading = "Total Loan Term (days)";
		String loanRateHeading = "Monthly Interest Rate";
		String totalLoanRateHeading = "Total Interest Rate";
		String payableAmountHeading = "Amount to Repay";
		String loanCreditorHeading = "Creditor Username";
		String s = ""; // the output variable of this function

		// if barrowerName isn't equal NONE in a loan then it's a loan that has been taken.
		int longestLoanNameCount=loanNameHeading.length();
		ArrayList<Loan> loanList = new ArrayList<>();
		for(Loan l : loans) {
			// Only show loan item for current user
			if (!l.getCreditorUserName().equals(customer.getUserName()) && !l.getBorrowerName().equals(customer.getUserName())) {
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

		int longestTotalRateCount=totalLoanRateHeading.length();
		for(int i=0; i<longestTotalRateCount-3; i++){
			totalLoanRateHeading += " ";
		}

		int payableAmountCount=payableAmountHeading.length();
		for(int i=0; i<payableAmountCount-3; i++){
			payableAmountHeading += " ";
		}

		int longestCreditorCount=loanCreditorHeading.length();
		for(int i=0; i<longestCreditorCount-3; i++){
			loanCreditorHeading += " ";
		}

		s += loanNameHeading+"        "+loanAmountHeading+"        "+loanTermHeading+"        "+loanRateHeading+"        "+
				totalLoanRateHeading+"        "+payableAmountHeading+"        "+loanCreditorHeading+"\n";

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
			s += "      " + l.getPrincipalAmount() + "£  ";
			s += "        ";
			s += l.getLoanTerm();
			s += "        ";
			s += l.getMonthlyInterestRate();
			s += "        ";
			s += String.format("%.4f", l.getTotalInterestRate());
			s += "        ";
			s += String.format("%.2f", l.getPayoffAmount()) + "£  ";
			s += "        ";
			s += l.getCreditorUserName();
			s += "\n";
			counter+=1;
		}
		// return output
		return s; }


	// retrieve loan from loan HashMap
	private Loan getCurrentLoan(String loanName){
		for(Loan loan : loans){
			if(loan.getLoanName().equals(loanName)){
				return loan;
			}
		}
		return null;
	}

}
