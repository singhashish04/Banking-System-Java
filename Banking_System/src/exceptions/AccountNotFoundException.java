package exceptions;

public class AccountNotFoundException extends RuntimeException{
	public AccountNotFoundException(String message) {
		super(message);
	}
}


// i make custom exception for future proof code,easy to read/debug, hanlde according to our requirement 