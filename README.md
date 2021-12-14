# New Bank Codebase

A repository created to store the code and version control the New Bank banking application project for the course unit Software Engineering 2 at the University of Bath.

# Authors

### Group 4

#### Group Members:

[Lorenz Saxler](https://github.com/lorenzsaxler)

[Mahtab Bahman Nejad](https://github.com/mahtabnejad90)

[Veronika Vysokozerskaia](https://github.com/matiek8) (Second GitHub username for Veronika Vysokozerskaia: https://github.com/RobynIvy)

[Rachel Berryman](https://github.com/rachelkberryman)

Copyright to New Bank Skeleton Code: [University of Bath](bath.ac.uk)

Code Owners of Further Implementations to skeleton code: [MSc Computer Science - Software Engineering 2 - Group 4](https://github.com/orgs/msc-computer-science-group-4/teams/group-4/members)

# Tech Stack

- Java - Programming Language
- IntelliJ - IDE
- Trello - Agile Project Management Board
- GitHub - Version Control
- Slack - Communication & Collaberation Tool

# Team Conventions

## Workflow Management

This team is using the Kanban method as the workflow management system. Because of the flexibility Kanban offers. As the whole team is responsible for the project management board, we thought that Kanban is the best practice to use. In terms of distribution of tasks, we ensure that the 'in progress' column is completed before we add additional tasks to it. If there was a bottleneck for whatever reason, other team members can pitch in and support the development of the task even if their roles are different.

## Code reviews

We have created a GitHub rule which allows major code changes to be merged into main branch only if the individual has submitted a pull request via GitHub and their code has been reviewed & approved by at least 2 members of the team.

## Team Ceremonies

We have a weekly 30 minute video calls to go over the changes of roles within the team as well as maintain the board. In these 30 minutes sessions we also go over any aspects that needs to be clarified within the team or passed to the originator.

# Application setup and navigation

In this section we will walk you through how to setup and navigate through the New Bank Java application.

## Setup Google Authentication

Before initiating the NewBank app, you will need to install the Google Authentication Application on a mobile device (iOS or Android), when you have completed the installation follow these steps:

1. Tap on `Get Started`
2. Select `Enter a setup key`
3. On the `Account` text field enter any custom name (e.g. MyName)
4. On the `Key` text field enter this exact key: `NY4A5CPJZ46LXZCP`
5. Make sure the account is set on `Time-based` from the drop down menu
6. Tap on the `Add` button

## Executing the NewBank App

In order to run the NewBank Java application, you will first need to make sure you have at least Java SDK 11 installed in your designated IDE. You will then have to run the following commands:

1. Open a terminal from the root of the New-Bank-Group-4 project
2. In the terminal enter the `javac newbank/server/NewBankServer.java` command to compile the code
3. Enter the `java newbank/server/NewBankServer.java` command on the same terminal
4. Then you should get a message on the terminal stating that the New Bank Server is listening to a local port number. Leave this terminal open and move on to the next step
5. Open a second terminal from the root of the New-Bank-Group-4 project
6. Enter the `javac newbank/client/ExampleClient.java ` command
7. Enter `java newbank/client/ExampleClient.java ` command on the same second terminal
8. You should now be presented to create a new user or sign in on the second terminal
9. Move on to `Create a new user` or `Logging in`sections of this documentation for more information

**Note: if you want to close the app at this point you can just enter `CTRL + C`**

## Create a new customer

In this instance we are going to demonstrate how to create a new customer and in order to do that, the following steps need to be taken:

1. From the New Bank Main Menu enter the value `2` in the terminal and press the enter
2. Enter your name (e.g. Example Customer) in the terminal and press the enter
3. Enter a new username (e.g. examplecustomer) in the terminal and press enter
4. Enter a new password (e.g. @Test1234)
5. Then you should get a prompt stating `User: 'examplecustomer' Created` and then be redirect to the New Bank Main Menu again
6. Move on to the Logging in section of this documentation

**Do not exit the application or close any terminals if you want to login with the username you just created**

## Logging in

In this intance we are going to demonstrate how to sign in with an existing account with the following step by step guide:

1. From the New Bank Main Menu enter the value `1` in the terminal and press enter
2. Enter an existing username already created (e.g. examplecustomer) and press enter
3. Enter the password that was set for that username (e.g. @Test1234) and press enter
4. If the correct values were submitted, then the application will return a `You are successfully logged in. What do you want to do now?` alongside a set of different commands the customer can choose from.

## Logged in User Protocals & Commands

The following user input and navigation is implemented within the run() method within the server/NewBankClientHandler.java class

Majority of the methods for these commands are based in the server/NewBankClient.java class, however there are some that are implemented in other classes.

1. Show My Accounts - Java method associated to it: showMyAccounts()
2. Transfer to another user - Java method associated to it: transferToUser()
3. Transfer to another owned account - Java method associated to it transferToSelf()
4. Create New Account - Java method associated to it createLoginAccount()
5. Close an Account - Java method associated to it: closeAccount() (based within the server/Customer.java class)
6. Add Funds to an Account - Java method associated to it addMoneyToAccount() (based within the server/NewBank.java class)
7. Show NewBank Loan Ledger (all customers) - Java method associated to it: loansToString() in server/NewBank.java
8. Offer loan - Java method associated to it - Java method associated to it: offerLoan()
9. Take out a Loan - Java method associated to it - Java methods associated to it: selectLoan() and run() within the server/NewBankClientHandler.java
10. Withdraw a Loan Offer - Java method associated to it - selectLoan() and run() within the server/NewBankClientHandler.java
11. Repay a Loan - Java Method associated to it repayLoan() within server/NewBank.java class
12. Log out - Conditional within the run() method

### Create New Bank Account command

To create a new bank account follow the below steps:

1. From the list of user commands enter `4` to create a new bank account and press enter
2. Enter `1` to create a current account
3. Enter a customised name you can remember your current account (e.g. example-currect-account) and press enter
4. Enter an amount that you would like to deposit within the account (e.g. 7000) and press enter
5. Then you should get a message similar to this `Successfully created a new Current Account named 'Example Current Account' with an initial balance of 7000.0£`
6. You will then be returned to the list of customer commands.

**Note: When adding a custom name to your bank account, make sure there's no spaces in between the characters**

### Displaying all bank accounts

For the displaying of bank accounts we have implemented an IBAN method which associatesa customer with a custom bank account id as soon as the customer creates a bank account. To see how the customer can view their bank account/s, please follow the below steps:

1. From the user command enter `1` 
2. You should now be presented with a list of created bank accounts including their IBAN
3. If you wish to transfer funds to this account from an external account (different username), please make sure you keep a note of the sender and receiver IBAN. You will not need the IBAN in order to transfer between accounts linked to the same username.
4. You will be shortly redirected back to the user logged in menu

### Transfer to another user

1. From the list of user commands enter the value `2`
2. Enter the username of receiver username which has already been created and has a bank account (e.g. receiverUser) and press enter
3. Enter the IBAN of the receiver you want to transfer funds to and press enter
4. Enter your own IBAN number you want to transfer funds from (enter the numbered list value, not the actual IBAN number)
5. Enter the amount you want to transfer 
6. Open you Google Authenticator app and enter the 6 digit number presented to you on the terminal and press enter
7. You should be presented with a similar message like this: `Successfully sent 3.0£ from your account 'ExampleAccount' to account GB24NWBK99999974358045.`
8. You will then be redirected to the user commands menu 

### Loan feature

#### Show NewBank Loan Ledger

When entering the value `7` from the logged in user commands menu, you will be redirected to a terminal which gives the option to the user to view the offered loans to taken out loans.

#### Offer Loan

In this instance, the customer can offer a loan and be a creditor. The loan that they offer will be taken from any account that has funds within it. To use this feature follow these steps:

1. From the user logged in menu input the value `8` and press enter
2. Choose the account you would like to offer a loan from and press enter
3. Input the amount you would like to offer (e.g. 5000) and press enter
4. Input the interest rate you'de like to charge (0.035 means 3.5% interest)
5. Input the loan term (number of days) and press enter
6. You should receive a success message that the loan has been offered and be redirected to the user logged in menu page

#### Take out Loan

The user is able to take out the loan that is offered to them. A guard has been put in so that the user does not take out a loan that they've offered already. We have added some hardcoded test data so that a user is able to take out a loan. To take out a loan, do the following:

1. From the user logged in menu input the value `9` and press enter
2. Input the ID of the loan you would like to take and press enter
3. Input the ID of the bank account you want the loan to be transferred into and press enter
4. You should then be presented a message similar to this:
      
       You have successfully taken out the loan with the Name: 'loan-spareuser-2021-12-09'.
       The Loan amount of ****.*£ was added to the account: 'Main'. 
       You have to repay the creditor a total amount of ****.**£ by YY-MM-DD.
       
5. You should then be be redirected to the user logged in menu page

#### Withdraw a Loan Offer

This feature enable the creditor to withdraw any loans that they've offered. In order to withdraw an offer please execute the following:

1. From the user logged in menu input the value `10` and press enter
2. Input the loan ID you want to withdraw from the list of offered loans
3. Input the account ID you would like to add the funds back to
4. You should get a success message the that funds have been added back to the selected account
5. You should be redirected back to the the user logged in menu

#### Repay Loan

This feature allows the user to repay the loan back to the creditor. To use this feature follow these steps below:

1. From the user logged in menu input the value `11` and press enter
2. You will be presented with a tabular list that specifies the loans that you can repay
3. Input the loan repayment ID and press enter
4. Enter the bank account ID you want to repay the loan from
5. Enter the amount you want to pay back
6. You should get a success message that you have paid off the amount specified
