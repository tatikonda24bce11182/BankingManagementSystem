# Banking Management System

## Overview
A Java-based banking application that simulates core banking operations including account management, transactions, and report generation.

## Features
- Account creation and management (Savings & Current accounts)
- Deposit, withdrawal, and fund transfer operations
- Transaction history and account statements
- Secure login and authentication
- MySQL database integration

## Technologies Used
- **Language:** Java (JDK 11)
- **Database:** MySQL
- **JDBC:** mysql-connector-java
- **IDE:** VS Code / IntelliJ IDEA

## Project Structure
```
BankingManagementSystem/
├── src/
│   ├── Main.java
│   ├── Account.java
│   ├── Customer.java
│   ├── Transaction.java
│   └── DatabaseConnection.java
├── database/
│   └── schema.sql
├── docs/
│   └── diagrams/
└── README.md
```

## Installation & Setup

1. Clone the repository:
```bash
git clone https://github.com/tatikonda24bce11182/BankingManagementSystem.git
```

2. Set up MySQL database:
   - Create database: `banking_system`
   - Run the schema.sql file from database folder

3. Update database credentials in `DatabaseConnection.java`

4. Compile and run:
```bash
javac src/*.java
java src/Main
```

## How to Use
1. Run the Main.java file
2. Choose from the menu options:
   - Create new account
   - Login to existing account
   - Perform transactions
   - View transaction history
   - Generate account statement



## Author
Tatikonda Aditya - VIT Student
