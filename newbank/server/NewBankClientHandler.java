
package newbank.server;

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
				out.println("Enter Your Name Please?");
				String customerName = in.readLine();
				out.println("Enter an available username:");
				String userName = in.readLine();
				out.println("Create a password:");
				String password = in.readLine();

				String request = "ACCOUNTCREATION" + "," + customerName + "," + userName + "," + password;
				String response = bank.processAccountCreationRequest(request);
				out.println(response);          // direct to account creation where they will be able to choose a username and continue
				while(response == "Weak password choice, please re-enter a stronger password:"){
					password = in.readLine();
					request = "ACCOUNTCREATION" + "," + customerName + "," + userName + "," + password;
					response = bank.processAccountCreationRequest(request);
					out.println(response);
				}

				while (response == "The username already exists.\nPlease enter a unique username or type 'esc' to return to the menu screen.") {
					userName = in.readLine();
					if (userName.equals("esc")){
						out.println("Loading menu screen...\n");
						sleep();
						break;
					}
				}
				if (!userName.equals("esc")) {
					clearScreen();
					out.println("User: '" + userName + "' Created\n");
					out.println("MENU LOADING...\n");
					sleep();
					run();
				} else if (userName.equals("esc")){
					out.println("MENU LOADING...\n");
					sleep();
					run();
				}

			}
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
					out.println("Enter the Name of the customer who's receiving the funds: ");
					String receiver = in.readLine();

					out.println("Enter the IBAN of the customer who's receiving the funds: ");
					String iban = in.readLine();

					out.println("Enter the Amount you'de like to transfer:  ");
					String transfarableSum = in.readLine();

					boolean valid = false;
					while(!valid){
						try{
							int check = Integer.parseInt(transfarableSum);
							valid = true;
						}catch (NumberFormatException ex) {
							out.println("Amount is invalid, please try again.\n");
							out.println("Enter the Amount you'de like to transfer:  ");
							transfarableSum = in.readLine();
						}
					}

					out.println("Enter the Account that you want to transfer from:  ");
					String accountName = SelectAccount(customer);

					request += "," + receiver + "," + iban + "," + transfarableSum + "," + accountName + ",";

					String response = bank.processRequest(customer, request);
					out.println(response);
					returnToMenu();

				} else if (request.equals("3")){
					clearScreen();
					out.println("Enter the Account that you want to transfer from:  ");
					String account_from = SelectAccount(customer);

					out.println("Enter the Account that you want to transfer to:  ");
					String account_to = SelectAccount(customer);

					out.println("Enter the Amount to transfer:  ");
					String transfarableSum = in.readLine();

					boolean valid = false;
					while(!valid){
						try{
							int check = Integer.parseInt(transfarableSum);
							valid = true;
						}catch (NumberFormatException ex) {
							out.println("Invalid input, please try again.\n");
							out.println("Enter the Amount to transfer:  ");
							transfarableSum = in.readLine();
						}
					}

					out.println("Please type in the 6-digit authentication number shown in your Google Authenticator App");
					String authNumber = in.readLine();

					request += "," + account_from + "," + account_to + "," + transfarableSum + "," + authNumber;

					String response = bank.processRequest(customer, request);
					out.println(response);
					returnToMenu();

				} else if (request.equals("5")) {
					clearScreen();

					// check if user has accounts to close
					if (Integer.parseInt(bank.processRequest(customer, "NUMBEROFUSERACCOUNTS")) == 0) {
						out.println("The user has no available accounts!\n");
					} else if (Integer.parseInt(bank.processRequest(customer, "NUMBEROFUSERACCOUNTS")) == 1) {
						// does not allow the user to close account if only one account remains
						out.println("The user must have at least one bank account with New Bank!");
					} else {
						// show accounts
						out.println(bank.processRequest(customer, "1"));

						// get the chosen account to close
						out.println("Select which account you wish to close (Type in the number on the left of the account name):\n");
						String accountToClose = in.readLine();
						request += "," + accountToClose;

						String accountToTransferFundsTo;

						// get the chosen account to transfer money to receiver
						do {
							clearScreen();
							out.println(bank.processRequest(customer, "1"));
							out.println("Select the bank account you would like to transfer the pending funds to:\n");
							accountToTransferFundsTo = in.readLine();
							if (accountToTransferFundsTo.equals(accountToClose)) {
								out.println("Can't be the same amount!.\n");
							}
						} while (accountToTransferFundsTo.equals(accountToClose));
						request += "," + accountToTransferFundsTo;

						// send the request and get the response
						String response = bank.processRequest(customer, request);
						out.println(response);
					}

					// return menu
					returnToMenu();
				} else if (request.equals("4")){
					clearScreen();
					out.println("Please select an account type:\n");
					out.println("1. Current Account");  // take account type
					out.println("2. Savings Account");
					String accountType = in.readLine();

					out.println("Please enter how much do you want to deposit to this new account:\n");
					String amountToAdd = in.readLine();

					boolean valid = false;
					while(!valid){
						try{
							int check = Integer.parseInt(amountToAdd);
							while (check==0.0){
								out.println("Account cannot be created without any added fund.\n");
								out.println("Please enter how much do you want to deposit to this new account:\n");
								amountToAdd = in.readLine();
								check = Integer.parseInt(amountToAdd);
							}
							valid = true;
						}catch (NumberFormatException ex) {
							out.println("Please try again, invalid input.\n");
							out.println("Please enter how much do you want to deposit to this new account:\n");
							amountToAdd = in.readLine();
						}
					}

					request += "," + accountType + "," + amountToAdd;
					while (!(accountType.equals("1")) && (!(accountType.equals("2")))) {
						// ensure customer's entry is valid
						out.println("Please try again");
					}
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
				} else if (request.equals("8")) {
					clearScreen();
					out.println("Logging out...");
					sleep();
					run();
				} else if (request.equals("9")){
					clearScreen();
					out.println("Thank you and have a nice day!");
					System.exit(0);
				}
				else if(!request.equalsIgnoreCase("10")) {
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
				"6. Log out\n" +
				"7. Quit\n";
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
		Thread.sleep(3000);
	}

	private String SelectAccount(CustomerID customer) throws Exception {
		//out.println("Enter the Account that you want to transfer from:  ");
		String selectableAccounts = bank.processRequest(customer, "DISPLAYSELECTABLEACCOUNTS");
		String option = "";
		String[] listOfSelections = selectableAccounts.split("\\n");
		boolean b = true;
		while (b){
			try{
				out.println(selectableAccounts);
				option = in.readLine();
				option = option.trim();
				while (Integer.parseInt(option) > listOfSelections.length ||
						Integer.parseInt(option) <= 0){
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
		String accountName = listOfSelections[Integer.parseInt(option)-1].substring(
				selectableAccounts.indexOf(". "));

		return accountName.substring(accountName.indexOf(" ")+1);
	}

}
