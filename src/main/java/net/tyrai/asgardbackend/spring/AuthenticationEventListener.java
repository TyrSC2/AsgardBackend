package net.tyrai.asgardbackend.spring;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import net.tyrai.asgardbackend.user.repository.User;
import net.tyrai.asgardbackend.user.repository.UserRepository;

@Component
public class AuthenticationEventListener {

	@Autowired
	private UserRepository userRepository;
	
    @EventListener
    public void authenticationFailed(AuthenticationFailureBadCredentialsEvent event) {

        String username = (String) event.getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(username);
        if (user != null) {
        	user.setFailedLogins(user.getFailedLogins() + 1);
        	user.setLastFailedLoginDate(new Date());
        	userRepository.save(user);
        }
    }
    

    @EventListener
    public void authenticationSuccesful(AuthenticationSuccessEvent event) {
    	MyUserPrincipal userPrincipal = (MyUserPrincipal) event.getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userPrincipal.getUsername());
        if (user != null) {
        	user.setFailedLogins(0);
        	userRepository.save(user);
        }
    }

}
