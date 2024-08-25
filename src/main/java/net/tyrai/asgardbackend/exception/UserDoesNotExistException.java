package net.tyrai.asgardbackend.exception;

public class UserDoesNotExistException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserDoesNotExistException(String email) {
		super("UserDoesNotExistException", "No user exists with email " + email + ".");
	}
}
