package net.tyrai.asgardbackend.spring;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
	private String[] publicEndpoints = new String[] {
			"/users/confirm/**",
			"/users/resetpassword/**",
			"/users/sendresetpassword/**",
			"/users/csrftoken",
			"/users/register/**",
			"/payment/webhook/**",
			"/routing/geocode/**",
			"/routing/load-signs",
			"/routing/set-segments",
			"/monitoring"
	};
	
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().ignoringAntMatchers(publicEndpoints)
        .and().authorizeRequests().antMatchers("/users/register/**").permitAll()
        .antMatchers("/users/confirm/**").permitAll()
        .antMatchers("/users/resendconfirm/**").permitAll()
        .antMatchers("/users/sendresetpassword/**").permitAll()
        .antMatchers("/users/resetpassword/**").permitAll()
        .antMatchers("/users/csrftoken").permitAll()
        .antMatchers("/users/logout").permitAll()
        .antMatchers("/routing/geocode/**").permitAll()
        .antMatchers("/routing/autocomplete/**").permitAll()
        .antMatchers("/routing/location/**").permitAll()
        .antMatchers("/payment/methods").permitAll()
        .antMatchers("/payment/testmail/**").permitAll()
        .antMatchers("/monitoring").permitAll()
        .antMatchers("/payment").fullyAuthenticated()
        .antMatchers("/users/delete").fullyAuthenticated()
        .antMatchers("/users/changepassword").fullyAuthenticated()
        .antMatchers("/users/login").fullyAuthenticated()
        .antMatchers("/routing/load-signs").fullyAuthenticated()
        .antMatchers("/routing/set-segments").fullyAuthenticated()
        .and().httpBasic();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) 
      throws Exception {
        auth.authenticationProvider(authProvider());
    }
    @Bean
    public DaoAuthenticationProvider authProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }
    

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
		configuration.setAllowCredentials(true);
		// the below three lines will add the relevant CORS response headers
		configuration.addAllowedOrigin("*");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("*").allowedOrigins("http://localhost:4200", "http://cvbouwen.nl", "https://cvbouwen.nl");
			}
		};
	}
}
