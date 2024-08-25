package net.tyrai.asgardbackend.exception;

public class InvalidTokenException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidTokenException(String message) {
		super("InvalidTokenException", message);
	}
}
