# New Bank Codebase

A repository created to store the code and version control the New Bank banking application project for the course unit Software Engineering 2 at the University of Bath.

# Authors

### Group 4

#### Group Members:

[Lorenz Saxler](https://github.com/lorenzsaxler)

[Mahtab Bahman Nejad](https://github.com/mahtabnejad90)

[Veronika Vysokozerskaia](https://github.com/matiek8)

[Rachel Berryman](https://github.com/rachelkberryman)

# Tech Stack

- Java - Programming Language
- IntelliJ - IDE
- Trello - Agile Project Management Board
- GitHub - Version Control
- Slack - Communication & Collaberation Tool

Copyright to New Bank Skeleton Code: [University of Bath](bath.ac.uk)

Code Owners of Further Implementations to skeleton code: [MSc Computer Science - Software Engineering 2 - Group 4](https://github.com/orgs/msc-computer-science-group-4/teams/group-4/members)

# Team Conventions

## Workflow Management

This team is using the Kanban method as the workflow management system. Because of the flexibility Kanban offers. As the whole team is responsible for the project management board, we thought that Kanban is the best practice to use. In terms of distribution of tasks, we ensure that the 'in progress' column is completed before we add additional tasks to it. If there was a bottleneck for whatever reason, other team members can pitch in and support the development of the task even if their roles are different.

## Code reviews

We have created a GitHub rule which allows major code changes to be merged into main branch only if the individual has submitted a pull request via GitHub and their code has been reviewed & approved by at least 2 members of the team.

## Team Ceremonies

We have a weekly 30 minute video calls to go over the changes of roles within the team as well as maintain the board. In these 30 minutes sessions we also go over any aspects that needs to be clarified within the team or passed to the originator.

# App navigation

In this section we will walk you through how to navigate within the app.

## Setup Google Authentication

Before initiating the NewBank app, you will need to install the Google Authentication Application on a mobile device (iOS or Android), when you have completed the installation follow these steps:

1 - Tap on `Get Started`
2 - Select `Enter a setup key`
3 - On the `Account` text field enter any custom name (e.g. MyName)
4 - On the `Key` text field enter this exact key: `NY4A5CPJZ46LXZCP`
5 - Make sure the account is set on `Time-based` from the drop down menu
6 - Tap on the `Add` button

## Starting the NewBank App

In order to run the NewBank Java application, you will first need to make sure you have at least Java SDK 11 installed in your designated IDE. You will then have to run the following commands:

1 - Open a terminal from the root of the New-Bank-Group-4 project
2 - In the terminal enter the `javac newbank/server/NewBankServer.java` command to compile the code
3 - Then enter the `java newbank/server/NewBankServer.java` command on the same terminal
4 - Then you should get a message on the terminal stating that the New Bank Server is listening to a local port number. Leave this terminal open and move on to the next step
5 - Open a second terminal from the root of the New-Bank-Group-4 project
6 - Enter the `javac newbank/client/ExampleClient.java ` command
7 - Enter `java newbank/client/ExampleClient.java ` command on the same second terminal
8 - You should now be presented to create a new user or sign in

Note: if you want to close the app at this point you can just enter `CTRL + C` 

## User Commands

