package domain;

public class Account {
	private String accountNumber;
	private String customerID;
	private Double balance;
	private String accountType;
	
	
	public Account(String accountNumber, String customerID, Double balance, String accountType) {
		this.accountNumber = accountNumber;
		this.customerID = customerID;
		this.balance = balance;
		this.accountType = accountType;
	}


	public String getAccountNumber() {
		return accountNumber;
	}


	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	public String getCustomerID() {
		return customerID;
	}


	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}


	public Double getBalance() {
		return balance;
	}


	public void setBalance(Double balance) {
		this.balance = balance;
	}


	public String getAccountType() {
		return accountType;
	}


	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	
}
