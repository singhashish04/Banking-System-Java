package exceptions;

public class ValidationException extends RuntimeException {
	public ValidationException(String meassage) {
		super(meassage);
	}
}
