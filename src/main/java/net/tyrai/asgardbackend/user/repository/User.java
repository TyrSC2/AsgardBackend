package net.tyrai.asgardbackend.user.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity(name = "User")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
	@Column(name = "password_hash", nullable = false)
    private String passwordHash;

	@Column(name = "email", nullable = false)
    private String email;
	
	@Column(name = "confirmation_token", nullable = true)
    private String confirmationToken;
	
	@Column(name = "confirmation_token_expiration_date", nullable = true)
    private Date confirmationTokenExpirationDate;

	@Column(name = "confirmed", nullable = false)
    private boolean confirmed;
	
	@Column(name = "reset_password_token", nullable = true)
    private String resetPasswordToken;
	
	@Column(name = "reset_password_token_expiration_date", nullable = true)
    private Date resetPasswordTokenExpirationDate;
	
	@Column(name = "failed_logins", nullable = false)
    private int failedLogins;
	
	@Column(name = "last_failed_login_date", nullable = true)
    private Date lastFailedLoginDate;

	@Column(name = "admin", nullable = false)
    private boolean admin;
	
	@Column(name = "mollie_id", nullable = true)
    private String mollieId;

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}
	
	public Date getConfirmationTokenExpirationDate() {
		return confirmationTokenExpirationDate;
	}

	public void setConfirmationTokenExpirationDate(Date confirmationTokenExpirationDate) {
		this.confirmationTokenExpirationDate = confirmationTokenExpirationDate;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public Date getResetPasswordTokenExpirationDate() {
		return resetPasswordTokenExpirationDate;
	}

	public void setResetPasswordTokenExpirationDate(Date resetPasswordTokenExpirationDate) {
		this.resetPasswordTokenExpirationDate = resetPasswordTokenExpirationDate;
	}

	public int getFailedLogins() {
		return failedLogins;
	}

	public void setFailedLogins(int failedLogins) {
		this.failedLogins = failedLogins;
	}

	public Date getLastFailedLoginDate() {
		return lastFailedLoginDate;
	}

	public void setLastFailedLoginDate(Date lastFailedLoginDate) {
		this.lastFailedLoginDate = lastFailedLoginDate;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getMollieId() {
		return mollieId;
	}

	public void setMollieId(String mollieId) {
		this.mollieId = mollieId;
	}

}
