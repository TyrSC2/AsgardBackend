package net.tyrai.asgardbackend.exception;

import org.json.JSONObject;

public abstract class BaseException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseException(String errorType, String message) {
		super(getJson(errorType, message));
	}
	
	public static String getJson(String errorType, String message) {
		JSONObject json = new JSONObject();
		json.put("errorType", errorType);
		json.put("message", message);
		return json.toString();
	}
}
