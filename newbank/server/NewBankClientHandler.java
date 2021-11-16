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

	//create user method
	public void createUser() {
		try {
			out.println("Please create a new username:");
			// reads the user input
			String userName = in.readLine();
			// new object for new customer
			Customer newCustomer = new Customer();
			// create a boilerplate account for the customer
			newCustomer.addAccount(new Account("Main", 00.0));
			// adds  customer to the list of existing customers and assigns them a username
			bank.customers.put(userName, newCustomer);
			out.println("User: '" + userName + "' Created");
		} catch (IOException e) {
			e.printStackTrace();
		}
		run();
	}

	public void run() {
		// try statement to get requests from the client side and renders them
		try {
			// Selection of login or create user ootions
			out.println("Please Choose From The Below Options:\n");
			out.println("1. Log In");
			out.println("2. Create User");
			String customerAction = in.readLine();

			while (!(customerAction.equals("1")) && (!(customerAction.equals("2")))) {
				// Customer entry validation
				out.println("Please try again");
				customerAction = in.readLine();
			}
			if (customerAction.equals("2")) {
				// redirects to account creation
				createUser();
			}
			if (customerAction.equals("1")) {
				// requests username if entry is 1
				out.println("Enter Username");
			}
			String userName = in.readLine();
			// requests for password entry
			out.println("Enter Password");
			String password = in.readLine();
			//generic customer message
			out.println("Retrieving Details...");
			// authenticates the user details and gets the customer ID token from bank for use in subsequent requests
			CustomerID customer = bank.checkLogInDetails(userName, password);

			// if the user is authenticated then get requests from the user and process them
			if(customer != null) {
				out.println("Sign in successful. What do you want to do next?");
				while(true) {
					out.println(showMenu());
					String request = in.readLine();
					if (request.equals("1")){
						//retrieves dashboard
						String dashboard = bank.processRequest(customer, "1");
						out.println(dashboard);
					} else if (request.equals("2")){
						out.println("Enter the Account that you want to amend the name for:  ");
						String accountName = SelectAccount(customer);

						// Request a new account name from user
						out.println("Please type in the new name for your selected account.");
						String newAccountName = in.readLine();
						newAccountName = newAccountName.trim();

						request += "," + accountName + "," + newAccountName;
						// Sends the request to the server and receives a response
						String response = bank.processRequest(customer, request);
						out.println(response);

					} else if(request.equals("3")){
						// Sends the request to the server and receives a response
						out.println("Enter the username of the sum receiver: ");
						String receiver = in.readLine();

						out.println("Enter the total amount to transfer to the receiver:  ");
						String amount_totransfer = in.readLine();

						out.println("Enter the account that you want to transfer the sum from:  ");
						String accountName = SelectAccount(customer);

						request += "," + receiver + "," + amount_totransfer + "," + accountName;

						// Send request to server and receive response
						String response = bank.processRequest(customer, request);
						out.println(response);

					} else if (request.equals("4")){

						out.println("Enter the account that you want to transfer from:  ");
						String account_from = SelectAccount(customer);

						out.println("Enter the account that you want to transfer to:  ");
						String account_to = SelectAccount(customer);

						out.println("Enter the amount to transfer:  ");
						String string_amount = in.readLine();

						request += "," + account_from + "," + account_to + "," + string_amount;
						// Send request to server and receive response
						String response = bank.processRequest(customer, request);
						out.println(response);

					} else if (request.equals("5")){
						out.println("Please select an account type:\n");
						out.println("1. Current Account");
						out.println("2. Savings Account");
						String accountType = in.readLine();
						request += "," + accountType;
						// verifies if customer entry is valid
						while (!(accountType.equals("1")) && (!(accountType.equals("2")))) {
							out.println("Please try again");
						}
						String response = bank.processRequest(customer, request);
						out.println(response);
					} else if (request.equals("6")){
						out.println("Thank you for using New Bank!");
						System.exit(0);
					} else if(!request.equalsIgnoreCase("6")) {
						out.println("Wrong entry, try.");
					} else {
						System.out.println("Request from " + customer.getKey());
						String responce = bank.processRequest(customer, request);
						out.println(responce);
					}
				}
			}
			else {
				out.println("Log In Failed.");
				out.println("Please type \"EXIT\" to terminate the Application.");
				String request = in.readLine();
			}
		} catch (IOException e) {
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

	private String showMenu()
	{
		//returns selection of options
		return "1. Show My Accounts\n2. Change Account Names\n3. Transfer to another user\n4. Transfer to another owned account\n5. Create New Account\n6. Quit";
	}

	private String SelectAccount(CustomerID customer){
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
				out.println("Please enter an number only!");
			}
		}
		// Get selected account name
		String accountName = listOfSelections[Integer.parseInt(option)-1].substring(
				selectableAccounts.indexOf(". "));

		return accountName.substring(accountName.indexOf(" ")+1);
	}

}
