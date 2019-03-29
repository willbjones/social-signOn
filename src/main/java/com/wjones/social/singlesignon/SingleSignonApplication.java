package com.wjones.social.singlesignon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Controller
public class SingleSignonApplication {

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;
	
	public static void main(String[] args) {
		SpringApplication.run(SingleSignonApplication.class, args);
	}

	@RequestMapping("/login")
	public String loginPage() {
		return "login.html";
	}
	
	@RequestMapping("/loginSuccess")
	public String loginSuccess(Model model, OAuth2AuthenticationToken authentication) {
		
		OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
				authentication.getAuthorizedClientRegistrationId(), authentication.getName());
		
		model.addAttribute("client", client.getClientRegistration().getClientName());
		
		if(client.getClientRegistration().getClientName().equalsIgnoreCase("facebook")) {
			Facebook fbApi = new Facebook(client.getAccessToken().getTokenValue());
			model.addAttribute("profile", fbApi.getProfile());
		}
		
		if(client.getClientRegistration().getClientName().equalsIgnoreCase("google")) {
			Google goApi = new Google(client.getAccessToken().getTokenValue());
			model.addAttribute("profile", goApi.getProfile());
		}
				
		return "loginSuccess";
	}
		
	@RequestMapping("/register")
	public String register(Model model) {
		RegisterForm form = new RegisterForm();
		model.addAttribute("registerForm", form);
		return "register.html";
	}
}
