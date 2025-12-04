package service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import domain.Account;
import domain.Customer;
import domain.Transaction;
import domain.Type;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.ValidationException;
import repository.AccountRepository;
import repository.CustomerRepository;
import repository.TransactionRepository;
import service.BankService;
import util.Validation;

public class BankServiceImpl implements BankService{
	
	private final AccountRepository accountRepository = new AccountRepository();
	private final TransactionRepository transactionRepository = new TransactionRepository();
	private final CustomerRepository customerRepository = new CustomerRepository();
	
	private final Validation<String> validateName = name -> {
		if(name == null || name.isBlank()) throw new ValidationException("Name is required");
	};

	private final Validation<String> validateEmail = email -> {
		if(email == null || !email.contains("@")) throw new ValidationException("Email is required");
	};
	
	private final Validation<String> validateType = type -> {
		if(type == null || !(type.equalsIgnoreCase("SAVINGS") || !type.equalsIgnoreCase("CURRENT"))) 
			throw new ValidationException("Account Type must be SAVINGS or CURRENT");
	};
	private final Validation<Double> validateAmountPositive = amount -> {
		if(amount == null || amount < 0) 
			throw new ValidationException("Please enter valid amount");
	};
	
	@Override
	public String openAccount(String name, String email, String accountType) {
		validateName.validate(name);
		validateEmail.validate(email);
		validateType.validate(accountType);
		
		String customerID = UUID.randomUUID().toString();
		
		//creating customer
		Customer c= new Customer(customerID, name, email);
		customerRepository.save(c);
		
		//Change later --> 10 + 1 = AC11
//		String accountNumber = UUID.randomUUID().toString();
		String accountNumber = getAccountNumber(); 
//		Account account= new Account(accountNumber, accountType, (double) 0, customerID);
		Account account = new Account(accountNumber, customerID, 0.0, accountType);

		// Save 
		accountRepository.save(account);
		
		
		return accountNumber;
	}

	private String getAccountNumber() {
		int temp = accountRepository.findAll().size() + 1;
		String accountNumber = String.format("AC%06d", temp);
		return accountNumber;
	}


	@Override
	public List<Account> listAccounts() {
		// TODO Auto-generated method stub
		return accountRepository.findAll().stream()
				.sorted(Comparator.comparing(Account::getAccountNumber))
				.collect(Collectors.toList());
	}

	@Override
	public void deposit(String accountNumber, Double amount, String note) {
		
		validateAmountPositive.validate(amount);
		
	    if (amount == null || amount <= 0) {
	        throw new IllegalArgumentException("Amount must be positive");
	    }

	    Account account = accountRepository.findByNumber(accountNumber)
	            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

	    // Update balance
	    account.setBalance(account.getBalance() + amount);

	    // Create transaction
	    Transaction transaction = new Transaction(
	            UUID.randomUUID().toString(),   // id
	            Type.DEPOSIT,                  // type
	            account.getAccountNumber(),    // accountNumber
	            amount,                        // amount
	            LocalDateTime.now(),           // timeStamp
	            note                           // note
	    );

	    transactionRepository.add(transaction);

	    // Re-save account (good practice)
	    accountRepository.save(account);
	}

	@Override
	public void withdraw(String accountNumber, Double amount, String note) {
		
		Account account = accountRepository.findByNumber(accountNumber)
	            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

		
		//check balance
		if(account.getBalance().compareTo(amount) < 0) 
			throw new InsufficientFundsException("Insufficient Balance");
	    // Update balance
	    account.setBalance(account.getBalance() - amount);

	    // Create transaction
	    Transaction transaction = new Transaction(
	            UUID.randomUUID().toString(),   // id
	            Type.WITHDRAW,                  // type
	            account.getAccountNumber(),    // accountNumber
	            amount,                        // amount
	            LocalDateTime.now(),           // timeStamp
	            note                           // note
	    );

	    transactionRepository.add(transaction);

	    // Re-save account (good practice)
	    accountRepository.save(account);
	}

	@Override
	public void transfer(String fromAcc, String toAcc, Double amount, String note) {
		if(fromAcc.equals(toAcc))
			throw new ValidationException("Cannot transfer to your own account");
		
		Account from = accountRepository.findByNumber(fromAcc)
	            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + fromAcc));
		
		Account to = accountRepository.findByNumber(toAcc)
	            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + toAcc));
		
		//check balance
		if(from.getBalance().compareTo(amount) < 0) 
			throw new InsufficientFundsException("Insufficient Balance");
		
		from.setBalance(from.getBalance() - amount);
		to.setBalance(to.getBalance() + amount);

	    transactionRepository.add(new Transaction(
	            UUID.randomUUID().toString(),   // id
	            Type.TRANSFER_OUT,                  // type
	            from.getAccountNumber(),    // accountNumber
	            amount,                        // amount
	            LocalDateTime.now(),           // timeStamp
	            note                           // note
	    ));
	    
	    transactionRepository.add(new Transaction(
	            UUID.randomUUID().toString(),   // id
	            Type.TRANSFER_IN,                  // type
	            to.getAccountNumber(),    // accountNumber
	            amount,                        // amount
	            LocalDateTime.now(),           // timeStamp
	            note                           // note
	    ));
		
	}

	@Override
	public List<Transaction> getStatement(String account) {

		return transactionRepository.findByAccount(account).stream()
				.sorted(Comparator.comparing(Transaction::getTimeStamp))
				.collect(Collectors.toList());
	}

	@Override
	public List<Account> searchAccountsByCustomerName(String q) {
		
		String query = (q==null) ? "" : q.toLowerCase();
//		List<Account> result = new ArrayList<>();
//		for(Customer c: customerRepository.findAll()) {
//			if(c.getName().toLowerCase().contains(query))
//				result.addAll(accountRepository.findByCustomerID(c.getId()));
//		}
//		result.sort(Comparator.comparing(Account::getAccountNumber));
//		return result;
		
		//same <=>
		
		return customerRepository.findAll().stream()
				.filter(c -> c.getName().toLowerCase().contains(query))
				.flatMap(c -> accountRepository.findByCustomerID(c.getId()).stream())
				.sorted(Comparator.comparing(Account::getAccountNumber))
				.collect(Collectors.toList());
		

	}



	
}

