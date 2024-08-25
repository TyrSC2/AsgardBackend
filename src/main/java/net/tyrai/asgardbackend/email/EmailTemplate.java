package net.tyrai.asgardbackend.email;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;

public enum EmailTemplate {
	
	ForgotPassword(
			"Reset password for Asgard", 
			"Greetings,<BR><BR>You are receiving this email because you requested a password reset.<BR>Please click the link below to reset your password:<BR><BR>https://asgard.tyr-ai.net/reset-password?token=%s&email=%s<BR><BR>Sincerely,<BR>Tyr",
			new MapBuilder()
			.get(),
			new MapBuilder()
			.get()
			);


	private String emailHead = "<!doctype html><html>" + 
			"<head>" + 
			"<style>" +
			"      .body {\r\n" + 
			"        background-color: #f6f6f6;\r\n" + 
			"        width: 100%%; \r\n" + 
			"      }\r\n" + 
			"\r\n" +  
			"      .container {\r\n" + 
			"        display: block;\r\n" + 
			"        margin: 0 auto !important;\r\n" + 
			"        /* makes it centered */\r\n" + 
			"        max-width: 650px;\r\n" + 
			"        padding: 10px;\r\n" + 
			"        width: 650px; \r\n" + 
			"      }" +
			"      .main {\r\n" + 
			"        background: #ffffff;\r\n" + 
			"        border-radius: 5px;\r\n" + 
			"        width: 100%%; \r\n" + 
			"      }" +
			"      .wrapper {\r\n" + 
			"        padding: 20px;\r\n" +
			"        box-sizing: border-box; \r\n" + 
			"      }" +
			"</style>" + 
			"</head>" + 
			"<body>" +
			"<table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\">\r\n" + 
			"<tr>\r\n" + 
			"<td>&nbsp;</td>\r\n" + 
			"<td class=\"container\">" + 
			"<table role=\"presentation\" class=\"main\">\r\n" + 
			"<tr>\r\n" + 
			"<td class=\"wrapper\">";
	
	private String emailFooter = "</td>\r\n" + 
			"</tr>\r\n" + 
			"</table>" + 
			"</td>\r\n" + 
			"<td>&nbsp;</td>\r\n" + 
			"</tr>\r\n" + 
			"</table>" + 
			"</body>" + 
			"</html>";
	
	public String title;
	public String body;
	Map<String, String> titleTranslations;
	Map<String, String> bodyTranslations;

	private EmailTemplate(String title, String body, Map<String, String> titleTranslations,
			Map<String, String> bodyTranslations) {
		this.title = title;
		this.body = body;
		this.titleTranslations = titleTranslations;
		this.bodyTranslations = bodyTranslations;
	}
	
	public static class MapBuilder {
		private Map<String, String> translations = new HashMap<>();
		
		public Map<String, String> get()
		{
			return translations;
		}
		
		public MapBuilder add(String language, String translation) {
			translations.put(language, translation);
			return this;
		}
	}
	
	public String getTitle(String language, Object ... arguments) {
		String title = titleTranslations.containsKey(language) ? titleTranslations.get(language) : this.title;
		return String.format(title, arguments);
	}
	
	public String getBody(String language, Object ... arguments) {
		String body = bodyTranslations.containsKey(language) ? bodyTranslations.get(language) : this.body;
		return emailHead + String.format(body, arguments) + emailFooter;
	}
	
	public MimeMessage getMailMessage(MimeMessage message, String language, String email, Object ... arguments) throws MessagingException {
		MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
		helper.setFrom("support@minikaart.nl");
		helper.setTo(email);
		helper.setSubject(getTitle(language));
		helper.setText(getBody(language, arguments), true);
        return message;
	}
}
