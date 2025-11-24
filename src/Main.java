import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankingSystem bankingSystem = new BankingSystem();
        
        System.out.println("================================================");
        System.out.println("   WELCOME TO BANKING MANAGEMENT SYSTEM");
        System.out.println("================================================");
        
        while (true) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Create New Account");
            System.out.println("2. Login to Existing Account");
            System.out.println("3. Exit");
            System.out.println("================================");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    bankingSystem.createAccount();
                    break;
                case 2:
                    Account account = bankingSystem.login();
                    if (account != null) {
                        bankingSystem.accountMenu(account);
                    }
                    break;
                case 3:
                    System.out.println("\nThank you for using Banking Management System!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option! Try again.");
            }
        }
    }
}