package com.wjones.social.singlesignon;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

@SpringBootApplication
@Controller
public class SingleSignonApplication {

	/*@Autowired
    private ClientRegistrationRepository clientRegistrationRepository;*/
	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;
	
	public static void main(String[] args) {
		SpringApplication.run(SingleSignonApplication.class, args);
	}

	@RequestMapping("/login")
	public String loginPage() {
		return "login.html";
	}
	
	
	@RequestMapping("/")
	public String homePage(WebRequest request, Model model) {
		
		/*ClientRegistration googleRegistration =
	            this.clientRegistrationRepository.findByRegistrationId("facebook");*/

		String username = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			username = ((UserDetails)principal).getUsername(); 
		} else {
			username = principal.toString();
		}
		model.addAttribute("username", username);
		return "home.html";
	}
	
	@RequestMapping("/loginSuccess")
	public String loginSuccess(Model model, OAuth2AuthenticationToken authentication) {
		
		OAuth2AuthorizedClient client = authorizedClientService
			      .loadAuthorizedClient(
			        authentication.getAuthorizedClientRegistrationId(), 
			          authentication.getName());
		String userInfoEndpointUri = client.getClientRegistration()
				  .getProviderDetails().getUserInfoEndpoint().getUri();
				 
		if (!StringUtils.isEmpty(userInfoEndpointUri)) {
			System.out.println("client Id: "+client.getClientRegistration().getClientName());
			if(client.getClientRegistration().getClientName().equalsIgnoreCase("facebook")) {
				userInfoEndpointUri = userInfoEndpointUri+",picture";
			}
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
			HttpEntity entity = new HttpEntity("", headers);
			ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
			Map userAttributes = response.getBody();
			model.addAttribute("client", client.getClientRegistration().getClientName());
			model.addAttribute("name", userAttributes.get("name"));
			model.addAttribute("picture", userAttributes.get("picture"));
			
		}
				
		return "loginSuccess";
	}
		
}
