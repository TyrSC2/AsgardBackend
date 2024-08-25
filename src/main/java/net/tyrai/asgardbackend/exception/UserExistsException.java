package net.tyrai.asgardbackend.exception;

public class UserExistsException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserExistsException(String email) {
		super("UserExistsException", "A user with email " + email + " already exists.");
	}
}
