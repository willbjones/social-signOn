package com.wjones.social.singlesignon;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
    protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		
		http
          .authorizeRequests()
            .antMatchers("/oauth_login", "/login**", "/logout**", "/register", "/oauth2/authorization/**", "/**/*.css", "/**/*.js", "/webjars/**").permitAll() /*, "/login/oauth2/code/**"*/
          .anyRequest().authenticated()
            .and()
          .oauth2Login().loginPage("/login").defaultSuccessUrl("/loginSuccess", true)
            .and()
          .logout().logoutSuccessUrl("/login");
    }
	
	
}
