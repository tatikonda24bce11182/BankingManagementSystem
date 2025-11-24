import java.sql.*;
import java.util.Scanner;

public class BankingSystem {
    private Scanner scanner;
    
    public BankingSystem() {
        this.scanner = new Scanner(System.in);
    }
    
    public void createAccount() {
        System.out.println("\n========== CREATE NEW ACCOUNT ==========");
        
        try {
            System.out.print("Enter Customer ID: ");
            String custId = scanner.nextLine();
            
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            
            System.out.print("Enter Phone: ");
            String phone = scanner.nextLine();
            
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            
            System.out.print("Account Type (SAVINGS/CURRENT): ");
            String accType = scanner.nextLine().toUpperCase();
            
            System.out.print("Initial Deposit Amount: ");
            double initialDeposit = scanner.nextDouble();
            scanner.nextLine();
            
            String accNumber = "ACC" + System.currentTimeMillis();
            
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            String custSql = "INSERT INTO customers (customer_id, name, email, phone, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement custStmt = conn.prepareStatement(custSql);
            custStmt.setString(1, custId);
            custStmt.setString(2, name);
            custStmt.setString(3, email);
            custStmt.setString(4, phone);
            custStmt.setString(5, password);
            custStmt.executeUpdate();
            
            String accSql = "INSERT INTO accounts (account_number, customer_id, account_type, balance) VALUES (?, ?, ?, ?)";
            PreparedStatement accStmt = conn.prepareStatement(accSql);
            accStmt.setString(1, accNumber);
            accStmt.setString(2, custId);
            accStmt.setString(3, accType);
            accStmt.setDouble(4, initialDeposit);
            accStmt.executeUpdate();
            
            conn.commit();
            
            System.out.println("\nAccount created successfully!");
            System.out.println("Your Account Number: " + accNumber);
            System.out.println("Initial Balance: Rs." + initialDeposit);
            
            DatabaseConnection.closeConnection(conn);
            
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }
    
    public Account login() {
        System.out.println("\n========== LOGIN ==========");
        
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine();
        
        System.out.print("Enter Customer ID: ");
        String custId = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        Connection conn = DatabaseConnection.getConnection();
        try {
            String sql = "SELECT a.*, c.password FROM accounts a JOIN customers c ON a.customer_id = c.customer_id WHERE a.account_number = ? AND a.customer_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accNum);
            pstmt.setString(2, custId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String dbPassword = rs.getString("password");
                
                if (password.equals(dbPassword)) {
                    Account account = new Account(
                        rs.getString("account_number"),
                        rs.getString("customer_id"),
                        rs.getString("account_type"),
                        rs.getDouble("balance")
                    );
                    
                    System.out.println("\nLogin successful! Welcome back!");
                    return account;
                } else {
                    System.out.println("Invalid password!");
                }
            } else {
                System.out.println("Account not found!");
            }
            
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        
        return null;
    }
    
    public void accountMenu(Account account) {
        while (true) {
            System.out.println("\n========== ACCOUNT MENU ==========");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. View Transaction History");
            System.out.println("6. Logout");
            System.out.println("==================================");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    account.checkBalance();
                    break;
                case 2:
                    System.out.print("Enter amount to deposit: Rs.");
                    double depositAmount = scanner.nextDouble();
                    scanner.nextLine();
                    account.deposit(depositAmount);
                    break;
                case 3:
                    System.out.print("Enter amount to withdraw: Rs.");
                    double withdrawAmount = scanner.nextDouble();
                    scanner.nextLine();
                    account.withdraw(withdrawAmount);
                    break;
                case 4:
                    System.out.print("Enter recipient account number: ");
                    String toAccount = scanner.nextLine();
                    System.out.print("Enter amount to transfer: Rs.");
                    double transferAmount = scanner.nextDouble();
                    scanner.nextLine();
                    account.transfer(toAccount, transferAmount);
                    break;
                case 5:
                    account.viewTransactionHistory();
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}