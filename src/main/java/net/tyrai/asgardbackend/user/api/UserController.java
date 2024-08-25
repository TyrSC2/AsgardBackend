package net.tyrai.asgardbackend.user.api;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.tyrai.asgardbackend.email.EmailTemplate;
import net.tyrai.asgardbackend.exception.InvalidTokenException;
import net.tyrai.asgardbackend.exception.UserDoesNotExistException;
import net.tyrai.asgardbackend.exception.UserExistsException;
import net.tyrai.asgardbackend.monitoringevent.api.MonitoringController;
import net.tyrai.asgardbackend.user.api.data.UserResponse;
import net.tyrai.asgardbackend.user.api.requests.ChangePasswordRequest;
import net.tyrai.asgardbackend.user.api.requests.DeleteUserRequest;
import net.tyrai.asgardbackend.user.api.requests.RegisterUserRequest;
import net.tyrai.asgardbackend.user.api.requests.ResetPasswordRequest;
import net.tyrai.asgardbackend.user.repository.User;
import net.tyrai.asgardbackend.user.repository.UserRepository;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SessionRegistry sessionRegistry;
	
	private SecureRandom secureRandom = new SecureRandom();
	
	@Autowired
	private JavaMailSender emailSender;
	
	@Autowired
	private MonitoringController monitoringController;

	@Value("${email.enabled}")
	private boolean emailEnabled;
	
	Logger logger = LoggerFactory.getLogger(UserController.class);
	
	/**
	 * Register a user.
	 *
	 * @param user The user to register.
	 * @return The created users email.
	 * @throws MessagingException 
	 * @throws MailException 
	 */
	@CrossOrigin
	@PostMapping("/register/{language}")
	public String registerUser(@RequestBody RegisterUserRequest registerUserRequest, @PathVariable(value = "language")String language) throws MailException, MessagingException {
		if (userRepository.existsUserByEmail(registerUserRequest.getEmail()))
			throw new UserExistsException(registerUserRequest.getEmail());

		
		String passwordHash = passwordEncoder.encode(registerUserRequest.getPassword());

		User user = new User();
		user.setEmail(registerUserRequest.getEmail());
		user.setPasswordHash(passwordHash);
		user.setConfirmed(false);
		generateConfirmationToken(user);

		userRepository.save(user);
		return user.getEmail();
	}
	
	@PostMapping("/delete")
	@Transactional
	public void deleteUser(Principal principal, @RequestBody DeleteUserRequest deleteUserRequest) {
		if (principal == null || principal.getName() == null)
			throw new IllegalArgumentException("You need to log in to delete your account.");

		User user = userRepository.findByEmail(principal.getName());
		if (!passwordEncoder.matches(deleteUserRequest.getPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Incorrect password.");
		}
		userRepository.delete(user);
	}
	
	@PostMapping("/changepassword")
	@Transactional
	public void changePassword(Principal principal, @RequestBody ChangePasswordRequest changePasswordRequest) {
		if (principal == null || principal.getName() == null)
			throw new IllegalArgumentException("You need to log in to change your password.");
		User user = userRepository.findByEmail(principal.getName());
		if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Incorrect password.");
		}
		
		String newPasswordHash = passwordEncoder.encode(changePasswordRequest.getNewPassword());
		user.setPasswordHash(newPasswordHash);
		userRepository.save(user);
		
		List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(principal, false);
		for (SessionInformation sessionInformation : sessionInformations) {
	        sessionInformation.expireNow();
	    }
	}
	
	@PostMapping("confirm/{email}/{token}")
	@Transactional
	public void confirmUser(@PathVariable(value = "email")String email, @PathVariable(value = "token")String token) {
		User user = userRepository.findByEmail(email);
		if (user.isConfirmed())
			return;
		if (user.getConfirmationToken() == null
				|| user.getConfirmationTokenExpirationDate() == null)
			throw new IllegalArgumentException("No confirmation token was set for this account. You can request a new account confirmation email through the website.");
		if (new Date().after(user.getConfirmationTokenExpirationDate())) {
			throw new IllegalArgumentException("The confirmation link for this account has expired. You can request a new account confirmation email through the website.");
		}
		
		if (!user.getConfirmationToken().equals(token)) {
			throw new IllegalArgumentException("The confirmation token for your account was incorrect. You can request a new account confirmation email through the website.");
		}
			
		
		user.setConfirmed(true);
		user.setConfirmationToken(null);
		user.setConfirmationTokenExpirationDate(null);
		userRepository.save(user);
	}
	
	@PostMapping("resendconfirm/{email}")
	@Transactional
	public void resendConfirmUser(@PathVariable(value = "email")String email) {
		User user = userRepository.findByEmail(email);
		if (user.isConfirmed())
			return;
		generateConfirmationToken(user);
		userRepository.save(user);
	}
	
	@PostMapping("sendresetpassword/{language}/{email}")
	@Transactional
	@CrossOrigin
	public void sendResetPasswordUser(@PathVariable(value = "email")String email, @PathVariable(value = "language")String language) throws MailException, MessagingException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UserDoesNotExistException(email);
		}
		generateResetPasswordToken(user);
		monitoringController.createEvent("Create reset password", "Reset password token " + user.getResetPasswordToken() + " created for user " + user.getEmail(), "ResetPassword", null, "backend");
		userRepository.save(user);

		if (emailEnabled)
			emailSender.send(EmailTemplate.ForgotPassword.getMailMessage(emailSender.createMimeMessage(), language, user.getEmail(), user.getResetPasswordToken(), user.getEmail()));
	}
	
	@PostMapping("resetpassword/{token}")
	@Transactional
	@CrossOrigin
	public void resetPasswordUser(@PathVariable(value = "token")String token, @RequestBody ResetPasswordRequest resetPasswordUserRequest) {
		User user = userRepository.findByEmail(resetPasswordUserRequest.getEmail());
		if (user == null) {
			monitoringController.createEvent("Unable to reset password", "User does not exist: " + resetPasswordUserRequest.getEmail() + " token was: " + token, "ResetPassword", null, "backend");
			throw new UserDoesNotExistException(resetPasswordUserRequest.getEmail());	
		}
		if (user.getResetPasswordToken() == null
				|| user.getResetPasswordTokenExpirationDate() == null) {
			if (user.getResetPasswordToken() == null)
				monitoringController.createEvent("Unable to reset password", "User does not have a reset password token. user: " + user.getEmail() + " token: " + token, "ResetPassword", null, "backend");
			else
				monitoringController.createEvent("Unable to reset password", "The reset password token has no expiration date. user: " + user.getEmail() + " token: " + token, "ResetPassword", null, "backend");
			throw new InvalidTokenException("There is no reset password token registered for the user.");
		}
		if (new Date().after(user.getResetPasswordTokenExpirationDate())) {
			monitoringController.createEvent("Unable to reset password", "The reset password token has expired. user: " + user.getEmail() + " token: " + token, "ResetPassword", null, "backend");
			throw new InvalidTokenException("The token has expired.");
		}
		if (!user.getResetPasswordToken().equals(token)) {
			monitoringController.createEvent("Unable to reset password", "The reset password token is invalid. user: " + user.getEmail() + " provided token: " + token + " registered token: " + user.getResetPasswordToken(), "ResetPassword", null, "backend");
			throw new InvalidTokenException("The provided token is invalid.");
		}
		user.setResetPasswordToken(null);
		user.setResetPasswordTokenExpirationDate(null);

		String passwordHash = passwordEncoder.encode(resetPasswordUserRequest.getPassword());
		user.setPasswordHash(passwordHash);
        user.setFailedLogins(0);
		userRepository.save(user);
		monitoringController.createEvent("Reset password succesful", "Succesfully reset password. user: " + user.getEmail() + " token: " + token, "ResetPassword", null, "backend");

		List<Object> principals = sessionRegistry.getAllPrincipals();
		for (Object principal : principals) {
			if (!((Principal)principal).getName().equals(resetPasswordUserRequest.getEmail()))
				continue;
			List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(principal, false);
			for (SessionInformation sessionInformation : sessionInformations) {
		        sessionInformation.expireNow();
		    }
		}
	}
	
	@GetMapping("/logout")
	public @ResponseBody ResponseEntity<?> logOut(HttpSession session) {
		session.invalidate();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("")
	public @ResponseBody List<UserResponse> getUsers(Principal principal) throws AuthenticationException {
		if (principal == null)
			throw new IllegalArgumentException("You must be logged in to use this endpoint.");
		User user = userRepository.findByEmail(principal.getName());
		if (!user.isAdmin())
			throw new AuthenticationException("You do not have access to this endopint.");
		List<User> users = userRepository.findAll();
		List<UserResponse> result = new ArrayList<>();
		for (User userIterator : users) {
			UserResponse userResponse = new UserResponse();
			userResponse.setEmail(userIterator.getEmail());
			
			result.add(userResponse);
		}
		return result;
	}
	
	@GetMapping("/is-admin")
	public @ResponseBody boolean isAdmin(Principal principal) {
		if (principal == null)
			return false;
		User user = userRepository.findByEmail(principal.getName());
		return user.isAdmin();
	}
	
	@GetMapping("/login")
	public @ResponseBody ResponseEntity<String> logIn(Principal principal, HttpServletRequest request) {
		if (principal == null || principal.getName() == null)
			throw new IllegalArgumentException("You need to log in.");
	    CsrfToken token = (CsrfToken)request.getAttribute(CsrfToken.class.getName());
		return new ResponseEntity<>(token.getToken(), HttpStatus.OK);
	}
	
	private void generateConfirmationToken(User user) {
		user.setConfirmationToken(generateToken(12));
		user.setConfirmationTokenExpirationDate(new Date(LocalDateTime.now().plusMonths(1).toEpochSecond(ZoneOffset.ofHours(0)) * 1000L ));
	}
	
	private void generateResetPasswordToken(User user) {
		user.setResetPasswordToken(generateToken(12));
		user.setResetPasswordTokenExpirationDate(new Date(LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.ofHours(0)) * 1000L ));
	}
	
	private String generateToken(int length)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++)
		{
			int next = secureRandom.nextInt(16);
			if (next < 10)
				sb.append(next);
			else
				sb.append((char)(next - 10 + 'a'));
		}
		return sb.toString();
	}
}
