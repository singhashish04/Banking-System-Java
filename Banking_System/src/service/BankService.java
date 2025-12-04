package service;

import java.util.List;

import domain.Account;
import domain.Transaction;

public interface BankService {
	String openAccount(String name, String email, String accountType);
	
	List<Account> listAccounts();

	void deposit(String accountNumber, Double amount, String string);

	void withdraw(String accountNumber, Double amount, String string);

	void transfer(String from, String to, Double amount, String string);

	List<Transaction> getStatement(String account);

	List<Account> searchAccountsByCustomerName(String q);
}
