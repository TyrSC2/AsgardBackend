package net.tyrai.asgardbackend.user.api.requests;

public class ResetPasswordRequest {
	private String email;
	private String newPassword;

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return newPassword;
	}
	public void setPassword(String password) {
		this.newPassword = password;
	}
}
