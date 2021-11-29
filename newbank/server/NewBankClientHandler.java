
package newbank.server;

import newbank.client.ExampleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread{

	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;

	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}

	public void run() {
		// keep getting requests from the client and processing them
		try {
			// creating and adding test user to the List as a first step
			bank.addTestData();

			clearScreen();
			out.println("Welcome to the New Bank Main Menu\n\nYou can choose from the below options:\n");      // offer log in or create account
			out.println("1. Sign In");
			out.println("2. Create a User");
			String customerAction = in.readLine();

			while (!(customerAction.equals("1")) && (!(customerAction.equals("2")))) {
				// ensure customer's entry is valid
				out.println("Please retry");
				customerAction = in.readLine();
			}
			if (customerAction.equals("2")) {
				out.println("Please enter your name:");
				String customerName = in.readLine();
				out.println("Please choose a username:");
				String userName = in.readLine();
				out.println("Create a password:");
				String password = in.readLine();

				String request = "ACCOUNTCREATION" + "," + customerName + "," + userName + "," + password;
				String response = bank.processAccountCreationRequest(request);
				out.println(response);          // direct to account creation where they will be able to choose a username and continue
				// if response starts with "Password" it means that a password error message was returned, so try again
				while(response.startsWith("Password ")){
					password = in.readLine();
					request = "ACCOUNTCREATION" + "," + customerName + "," + userName + "," + password;
					response = bank.processAccountCreationRequest(request);
					out.println(response);
				}

				while (response.equals("The username already exists.\nPlease enter a unique username or type 'esc'" +
						" to return to the menu screen.")) {
					userName = in.readLine();
					// capitalized the esc to also allow for Esc, ESC etc..
					if (userName.equalsIgnoreCase("ESC")){
						out.println("Loading menu screen...\n");
						sleep();
						break;
					} else {
						// process account creation request with new chosen username
						String newRequest = "ACCOUNTCREATION" + "," + customerName + "," + userName + "," + password;
						// get response for new request
						response = bank.processAccountCreationRequest(newRequest);
						out.println(response);
					}
				}
				// If username chosen was unique, and Esc was not requested then inform user about success
				if (!userName.equalsIgnoreCase("ESC")) {
					clearScreen();
					out.println("User: '" + userName + "' Created\n");
					out.println("Please login with your newly created user account...\n");
					sleep();
					run();
				} else { // if user typed Esc rerun thread
					out.println("MENU LOADING...\n");
					sleep();
					run();
				}

			}
			// Logging In for existing user:
			if (customerAction.equals("1")) {
				clearScreen();
				// Requests existing username
				out.println("Please enter your username");
			}
			String userName = in.readLine();
			// Requests existing password
			out.println("Please enter your password");
			String password = in.readLine();
			out.println("Hang on a second while we check your details...");

			// user authenatication
			CustomerID customer = bank.checkLogInDetails(userName, password);
			while (customer==null){
				out.println("Log In Failed. Please try again");
				out.println("Enter Username");
				userName = in.readLine();
				// request user for password again
				out.println("Enter Password");
				password = in.readLine();
				out.println("Hang on a second while we check your details again...");
				customer = bank.checkLogInDetails(userName, password);
			}
			// if user is successful they proceed with the next set of actions
			out.println("You are successfully logged in. What do you want to do now?");
			while(true) {
				out.println(showMenu());
				String request = in.readLine();
				if (request.equals("1")){
					clearScreen();
					String dashboard = bank.processRequest(customer, "1");
					out.println(dashboard);
					returnToMenu();
				} else if(request.equals("2")){
					clearScreen();
					out.println("Enter the username of the customer who is receiving the funds: ");
					String receiver = in.readLine();
					// added a check for existence of customer to transfer to
					boolean isCustomer = false;
					while(!isCustomer){
						if(bank.getCustomers().containsKey(receiver)){
							isCustomer = true;
						}else{
							out.println("Please enter a valid username!");
							receiver = in.readLine();
						}
					}

					out.println("Enter the IBAN of the customer Account which is receiving the funds: ");
					String iban = in.readLine();

					out.println("Enter the Amount you would like to transfer:  ");
					String transferableSum = in.readLine();

					boolean valid = false;
					while(!valid){
						try{
							int check = Integer.parseInt(transferableSum);
							valid = true;
						}catch (NumberFormatException ex) {
							out.println("Amount is invalid, please try again.\n");
							out.println("Enter the Amount you would like to transfer:  ");
							transferableSum = in.readLine();
						}
					}

					out.println("Enter the number next to the name of the Account you would like to transfer from:  ");
					String accountName = selectAccount(customer);

					request += "," + receiver + "," + iban + "," + transferableSum + "," + accountName + ",";

					String response = bank.processRequest(customer, request);
					out.println(response);
					returnToMenu();

				} else if (request.equals("3")){
					clearScreen();
					out.println("Enter the Account that you want to transfer from:  ");
					String account_from = selectAccount(customer);

					out.println("Enter the Account that you want to transfer to:  ");
					String account_to = selectAccount(customer);

					out.println("Enter the Amount to transfer:  ");
					String transferableSum = in.readLine();

					boolean valid = false;
					while(!valid){
						try{
							int check = Integer.parseInt(transferableSum);
							valid = true;
						}catch (NumberFormatException ex) {
							out.println("Invalid input, please try again.\n");
							out.println("Enter the Amount to transfer:  ");
							transferableSum = in.readLine();
						}
					}

					out.println("Please type in the 6-digit authentication number shown in your Google Authenticator App");
					String authNumber = in.readLine();

					request += "," + account_from + "," + account_to + "," + transferableSum + "," + authNumber;

					String response = bank.processRequest(customer, request);
					out.println(response);
					returnToMenu();

				} else if (request.equals("5")) {
					clearScreen();

					// check if user has accounts to close
					if (Integer.parseInt(bank.processRequest(customer, "NUMBEROFUSERACCOUNTS")) == 0) {
						out.println("You currently have no bank accounts under your name!\n");
					} else if (Integer.parseInt(bank.processRequest(customer, "NUMBEROFUSERACCOUNTS")) == 1) {
						// does not allow the user to close account if only one account remains
						out.println("You currently only have one account under your name.");
						out.println("You must have at least one bank account with New Bank!");

					} else { // 2 or more accounts under the users name
						// get the account to close
						out.println("Select which account you wish to close (Type in the number on the left of the account name):\n");
						String accountToClose = selectAccount(customer);
						/* Only allow user to close account if balance is >= 0.
						Decided to make a separate request so that the user does not have to go through all steps below and only
						then receive the information that it failed because of a negative balance */
						String accountBalanceRequest = "CHECKACCOUNTBALANCE" + "," + accountToClose;

						double remainingBalance = Double.parseDouble(bank.processRequest(customer, accountBalanceRequest));
						if (remainingBalance < 0) {
							out.println("You cannot close an account with a negative balance!");
						}
						// Else if balance is 0, proceed to close the account without fund transferring
						else if (remainingBalance == 0){
							String closeAccountRequest = "CLOSEACCOUNT" + "," + accountToClose;
							out.println(bank.processRequest(customer, closeAccountRequest));
						}

						else { // else if closing account has funds in it
							out.println("Select which account you wish to transfer the accounts remaining funds to" +
									" (Type in the number on the left of the account name):\n");
							String accountToTransferFundsTo = selectAccount(customer);

							// do not allow transferring to same account before closing
							while (accountToTransferFundsTo.equals(accountToClose)){
								out.println("Can't be the same account! Please choose a different account: to transfer to\n");
								accountToTransferFundsTo = selectAccount(customer);
							}

							String transferCloseRequest = "TRANSFERANDCLOSE" + "," + accountToClose +
									"," + accountToTransferFundsTo;

							// send the request and get the response
							String response = bank.processRequest(customer, transferCloseRequest);
							out.println(response);
						}
					}
					// return menu
					returnToMenu();

				} else if (request.equals("4")){
					clearScreen();
					out.println("Please select an account type:\n");
					out.println("1. Current Account");  // take account type
					out.println("2. Savings Account");
					String accountType = in.readLine();

					// moved account type checking to beginning of request
					while (!(accountType.equals("1")) && (!(accountType.equals("2")))) {
						out.println("Invalid account type.");
						out.println("Please enter either 1 to create a Current Account or 2 to create a Savings Account.");
						accountType = in.readLine();
					}

					// letting user choose an account name
					out.println("Please select a custom name for your account:\n");
					String accountName = in.readLine();


					out.println("Please enter how much you want to deposit to this new account:\n");
					String amountToAdd = in.readLine();

					boolean valid = false;
					while(!valid){
						try{
							int check = Integer.parseInt(amountToAdd);
							while (check <= 0){
								out.println("Account needs to be created with a positive initial balance.\n");
								out.println("Please enter how much you want to deposit to this new account:\n");
								amountToAdd = in.readLine();
								check = Integer.parseInt(amountToAdd);
							}
							valid = true;
						}catch (NumberFormatException ex) {
							out.println("Please try again, invalid input.\n");
							out.println("Please enter how much you want to deposit to this new account:\n");
							amountToAdd = in.readLine();
						}
					}

					request += "," + accountType + "," + accountName + "," + amountToAdd;
					String response = bank.processRequest(customer, request);
					out.println(response);

				} else if (request.equals("6")){
					out.println("Please type in the 6-digit authentication number shown in your Google Authenticator App");
					String authNumber = in.readLine();
					request += "," + authNumber;
					String response = bank.processRequest(customer, request);
					out.println(response);

				} else if (request.equals("7")){
					// cancel a scheduled transfer
					// show scheduled transfers
					out.println("Please type in the 6-digit authentication number shown in your Google Authenticator App");
					String authNumber = in.readLine();
					request = "6" + "," + authNumber;
					String response = bank.processRequest(customer, request);
					out.println(response);
					while (response.equals("Not able to show scheduled actions: Authentication fail")){
						out.println("Please type in the 6-digit authentication number shown in your Google Authenticator App");
						authNumber = in.readLine();
						request = "6" + "," + authNumber;
						response = bank.processRequest(customer, request);
						out.println(response);
					}
					// get id of transfer to be cancelled
					out.println("Enter number of transaction you wish to cancel:");
					String cancelTransaction = in.readLine();
					request = "7" + "," + cancelTransaction + "," + authNumber;

					response = bank.processRequest(customer, request);
					out.println(response);

				} else if (request.equals("8")){ // Adding funds to an Account
					out.println("Please enter the number next to the name of the Account you would like to " +
							"add funds to: ");
					/* there currently is a null response problem with the selectAccount method. It thus does not return
					a value. Before this command can be implemented, the selectAccount() method needs to be fixed */

					//selectAccount(customer);




				}
				else if (request.equals("9")) {
					clearScreen();
					out.println("Logging out...");
					sleep();
					run();
				}

				else if(request.equals("10")){
					out.println("Thank you and have a nice day!");
					System.exit(0);
				}

				else if(!request.equalsIgnoreCase("11")) {
					clearScreen();
					out.println("Invalid Entry\n");
				} else {
					System.out.println("Request from " + customer.getName());
					String responce = bank.processRequest(customer, request);
					out.println(responce);
					returnToMenu();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	private String
	showMenu() {
		return "Please choose from the options below: \n\n" +
				"1. Show My Accounts\n" +
				"2. Transfer to another user\n" +
				"3. Transfer to another owned account\n" +
				"4. Create New Account\n" +
				"5. Close an Account\n" +
				"8. Add Funds to an Account\n" +
				"9. Log out\n" +
				"10. Quit\n";
	}

	public void clearScreen() {
		out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"); // utilised while this function will not work in IntelliJ
		out.print("\033[H\033[2J");
		out.flush();
	}

	private void returnToMenu() throws InterruptedException { ;
		out.println("\nReturning to menu screen...");
		sleep();
		clearScreen();
		showMenu();
	}

	private void sleep() throws InterruptedException {
		Thread.sleep(2000);
	}

	/**
	 * This method displays a list of accounts of a specific customer,
	 * reads the index of the selected account and returns the name of the selected account
	 */
	private String selectAccount(CustomerID customer) throws Exception {
		//out.println("Enter the Account that you want to transfer from:  ");
		String selectableAccounts = bank.processRequest(customer, "DISPLAYSELECTABLEACCOUNTS");
		String numberOfAccounts = bank.processRequest(customer, "NUMBEROFUSERACCOUNTS");
		String option = "";
		boolean b = true;
		while (b){
			try{
				out.println(selectableAccounts);
				option = in.readLine();
				option = option.trim();
				while (Integer.parseInt(option) > Integer.parseInt(numberOfAccounts) || Integer.parseInt(option) <= 0){
					out.println("Please select a valid option:");
					out.println(selectableAccounts);
					option = in.readLine();
					option = option.trim();
				}
				b = false;
			}catch (NumberFormatException | IOException ex) {
				out.println("Please enter an integer only!");
			}
		}
		// Retrieve selected account name
		String request = "DISPLAYSELECTEDNAMEACCOUNT, " + (Integer.parseInt(option)-1);
		String selectedAccountName = bank.processRequest(customer, request);

		System.out.println("accountName " + selectedAccountName);
		return selectedAccountName;
	}

}
