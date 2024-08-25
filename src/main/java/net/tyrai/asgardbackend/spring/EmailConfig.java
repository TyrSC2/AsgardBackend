package net.tyrai.asgardbackend.spring;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {
	@Value("${email.username}")
	private String username;
	
	@Value("${email.password}")
	private String password;
	
	@Value("${email.host}")
	private String host;
	
	@Value("${email.port}")
	private Integer port;
    
	@Bean
    public JavaMailSender getJavaMailSender() 
    {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
          
        mailSender.setUsername(username);
        mailSender.setPassword(password);
          
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
          
        return mailSender;
    }

}
