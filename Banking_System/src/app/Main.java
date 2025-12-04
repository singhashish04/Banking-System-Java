package app;

import java.util.Scanner;

import domain.Account;
import service.BankService;
import service.impl.BankServiceImpl;

public class Main {
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);
		BankService bankService= new BankServiceImpl();
		System.out.println("Welcome  to Console Bank");
		boolean running = true;
		while(running) {
			System.out.println("""
					1) Open Account
					2) Deposit
					3) Withdraw
					4) Transfer
					5) Account Statement
					6) List Accounts
					7) Search Accounts by Customer Name
					0) Exit
				""");
			System.out.print("ChOOSE: ");
			String choice = scn.nextLine().trim();
			System.out.println("CHOOSE: "+ choice);
		
			switch(choice) {
			case "1" -> openAccount(scn, bankService);
			case "2" -> deposit(scn,bankService);
			case "3" -> withdraw(scn, bankService);
			case "4" -> transfer(scn, bankService);
			case "5" -> statement(scn, bankService);
			case "6" -> listAccounts(scn, bankService);
			case "7" -> searchAccounts(scn, bankService); 
			case "0" -> running = false;
			}
		}
	}
	
	private static void openAccount(Scanner scn, BankService bankService) {
	    System.out.println("Customer name: ");
	    String name = scn.nextLine().trim();

	    System.out.println("Customer email: ");
	    String email = scn.nextLine().trim();

	    System.out.println("Account Type (SAVINGS/CURRENT): ");
	    String type = scn.nextLine().trim();

	    System.out.println("Initial deposit (optional, blank for 0): ");
	    String amountStr = scn.nextLine().trim();

	    double initial = 0.0;
	    if (!amountStr.isEmpty()) {
	        try {
	            initial = Double.parseDouble(amountStr);
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid amount, treating as 0.");
	        }
	    }

	    String accountNumber = bankService.openAccount(name, email, type);

	    if (initial > 0) {
	        bankService.deposit(accountNumber, initial, "Initial deposit");
	    }

	    System.out.println("Account Opened: " + accountNumber);
	}


	private static void deposit(Scanner scn, BankService bankService) {
		System.out.println("Account number: ");
		String accountNumber = scn.nextLine().trim();
		System.out.println("Amount: ");
		Double amount = Double.valueOf(scn.nextLine().trim());
		bankService.deposit(accountNumber, amount, "Deposit");
		System.out.println("Deposited");
	}

	private static void withdraw(Scanner scn, BankService bankService) {
		System.out.println("Account number: ");
		String accountNumber = scn.nextLine().trim();
		System.out.println("Amount: ");
		Double amount = Double.valueOf(scn.nextLine().trim());
		bankService.withdraw(accountNumber, amount, "Withdrawal");
		System.out.println("Withdrawn");
	}

	private static void transfer(Scanner scn, BankService bankService) {
		System.out.println("From Account: ");
		String from = scn.nextLine().trim();
		System.out.println("To Account: ");
		String to = scn.nextLine().trim();
		System.out.println("Amount: ");
		Double amount = Double.valueOf(scn.nextLine().trim());
		bankService.transfer(from, to, amount, "Transfer");
		System.out.println("Transfer Successful");
	}

	private static void statement(Scanner scn, BankService bankService) {
		System.out.println("Account number: ");
		String account = scn.nextLine().trim();
		bankService.getStatement(account).forEach(t -> {
			System.out.println(t.getTimeStamp() + " | " + t.getType() + " | " + t.getAmount()+ " | " + t.getNote());
		});
	}

	private static void listAccounts(Scanner scn, BankService bankService) {
		bankService.listAccounts().forEach(a -> {
			System.out.println(a.getAccountNumber() + " | " + a.getAccountType() + " | " + a.getBalance());
		});
	}

	private static void searchAccounts(Scanner scn, BankService bankService) {
		System.out.println("Customer name Contains: ");
		 String q = scn.nextLine().trim();
		 bankService.searchAccountsByCustomerName(q).forEach(account -> 
		 		System.out.println(account.getAccountNumber() + " | " + account.getAccountType() + " | " + account.getBalance())
				 );
	}
}
