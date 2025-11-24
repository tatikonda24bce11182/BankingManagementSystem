import java.sql.*;

public class Account {
    private String accountNumber;
    private String customerId;
    private String accountType;
    private double balance;
    
    public Account(String accountNumber, String customerId, String accountType, double balance) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
    }
    
    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return false;
        }
        
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            String updateSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateSql);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
            
            this.balance += amount;
            recordTransaction("DEPOSIT", amount, this.balance, "", conn);
            
            conn.commit();
            System.out.println("Deposit successful! New balance: Rs." + this.balance);
            return true;
            
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.out.println("Deposit failed: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount!");
            return false;
        }
        
        if (amount > balance) {
            System.out.println("Insufficient balance!");
            return false;
        }
        
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateSql);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
            
            this.balance -= amount;
            recordTransaction("WITHDRAWAL", amount, this.balance, "", conn);
            
            conn.commit();
            System.out.println("Withdrawal successful! Remaining balance: Rs." + this.balance);
            return true;
            
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.out.println("Withdrawal failed: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
    
    public boolean transfer(String toAccountNumber, double amount) {
        if (amount <= 0 || amount > balance) {
            System.out.println("Invalid transfer amount!");
            return false;
        }
        
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            String checkSql = "SELECT * FROM accounts WHERE account_number = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, toAccountNumber);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("Recipient account not found!");
                return false;
            }
            
            String deductSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            PreparedStatement deductStmt = conn.prepareStatement(deductSql);
            deductStmt.setDouble(1, amount);
            deductStmt.setString(2, accountNumber);
            deductStmt.executeUpdate();
            
            String addSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement addStmt = conn.prepareStatement(addSql);
            addStmt.setDouble(1, amount);
            addStmt.setString(2, toAccountNumber);
            addStmt.executeUpdate();
            
            this.balance -= amount;
            recordTransaction("TRANSFER", amount, this.balance, "To: " + toAccountNumber, conn);
            
            conn.commit();
            System.out.println("Transfer successful! New balance: Rs." + this.balance);
            return true;
            
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.out.println("Transfer failed: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
    
    private void recordTransaction(String type, double amount, double balanceAfter, String desc, Connection conn) throws SQLException {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, balance_after, description) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, accountNumber);
        pstmt.setString(2, type);
        pstmt.setDouble(3, amount);
        pstmt.setDouble(4, balanceAfter);
        pstmt.setString(5, desc);
        pstmt.executeUpdate();
    }
    
    public void viewTransactionHistory() {
        Connection conn = DatabaseConnection.getConnection();
        try {
            String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC LIMIT 10";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("\n========== TRANSACTION HISTORY ==========");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("transaction_id"));
                System.out.println("Type: " + rs.getString("transaction_type"));
                System.out.println("Amount: Rs." + rs.getDouble("amount"));
                System.out.println("Balance After: Rs." + rs.getDouble("balance_after"));
                System.out.println("Date: " + rs.getTimestamp("transaction_date"));
                System.out.println("------------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
    
    public void checkBalance() {
        System.out.println("\n========== BALANCE INQUIRY ==========");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Type: " + accountType);
        System.out.println("Current Balance: Rs." + balance);
        System.out.println("=====================================\n");
    }
    
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
}