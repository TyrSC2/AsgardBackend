package net.tyrai.asgardbackend.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.tyrai.asgardbackend.user.repository.User;
import net.tyrai.asgardbackend.user.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    // 
    public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(
              "No user found with username: "+ username);
        }
        if (user.getFailedLogins() >= 10
        		&& user.getLastFailedLoginDate() != null
        		&& user.getLastFailedLoginDate().getTime() + 1000 * 60 * 60 * 24 >= System.currentTimeMillis()) {
        	throw new RuntimeException("Your account has been blocked for 24 hours as there have been too many failed login attempts.");
        }
        return  new MyUserPrincipal(user);
    }
}
