/*******************************************************************************
 * Copyright (c) 2016 Eviarc GmbH.
 * All rights reserved.  
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Eviarc GmbH and its suppliers, if any.  
 * The intellectual and technical concepts contained
 * herein are proprietary to Eviarc GmbH,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Eviarc GmbH.
 *******************************************************************************/

package dash;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cloud.aws.context.config.annotation.EnableContextRegion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.util.StopWatch;

import com.google.common.base.Predicate;

import dash.security.AngularCsrfHeaderFilter;
import dash.security.listener.RESTAuthenticationEntryPoint;
import dash.usermanagement.business.UserRepository;
import dash.usermanagement.domain.Role;
import dash.usermanagement.domain.User;
import dash.usermanagement.settings.language.Language;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableJpaRepositories
@EnableContextRegion(region = "eu-central-1")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	@ConditionalOnMissingBean
	public Docket multipartApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Lead-Management-REST-API").apiInfo(apiInfo()).select().paths(paths()).build();
	}

	@Bean
	public Docket vortoApi() {
		StopWatch watch = new StopWatch();
		watch.start();
		Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).useDefaultResponseMessages(false).select().paths(paths()).build();
		watch.stop();
		return docket;
	}

	@SuppressWarnings("unchecked")
	private Predicate<String> paths() {
		return or(regex("/api/rest/processes.*"), regex("/api/rest/processes/leads.*"), regex("/api/rest/processes/leads/containers.*"),
				regex("/api/rest/processes/leads/inquirers.*"), regex("/api/rest/processes/leads/vendors.*"), regex("/api/rest/processes/offers.*"),
				regex("/api/rest/processes/offers/prospects.*"), regex("/api/rest/processes/sales.*"), regex("/api/rest/processes/sales/customers.*"));
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Applica").description("Applica - Lead Management Tool").license("").licenseUrl("").version("1.0").build();
	}

	@Autowired
	private UserRepository userRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@PostConstruct
	public void createAdminIfNotExists() throws Exception {

		if (!Optional.ofNullable(userRepository.findByUsernameIgnoreCase("admin")).isPresent()) {
			User admin = new User();

			admin.setUsername("admin".toLowerCase());
			admin.setPassword(passwordEncoder().encode("6HzRSZjmj89sQaN5!"));
			admin.setEmail("admin@eviarc.com");
			admin.setRole(Role.SUPERADMIN);
			admin.setEnabled(true);
			admin.setLanguage(Language.DE);

			userRepository.save(admin);
		}

		if (!Optional.ofNullable(userRepository.findByUsernameIgnoreCase("api")).isPresent()) {
			User apiuser = new User();

			apiuser.setUsername("api".toLowerCase());
			apiuser.setPassword(passwordEncoder().encode("!APQYtDwgBtNqNY5L"));
			apiuser.setEmail("api@eviarc.com");
			apiuser.setRole(Role.USER);
			apiuser.setEnabled(true);
			apiuser.setLanguage(Language.DE);

			userRepository.save(apiuser);
		}

		if (!Optional.ofNullable(userRepository.findByUsernameIgnoreCase("test")).isPresent()) {
			User test = new User();

			test.setUsername("test".toLowerCase());
			test.setPassword(passwordEncoder().encode("test"));
			test.setEmail("test@eviarc.com");
			test.setRole(Role.USER);
			test.setEnabled(true);
			test.setLanguage(Language.DE);

			userRepository.save(test);
		}
	}

	@Configuration
	@EnableWebSecurity
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	public static class SecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private UserDetailsService userDetailsService;

		@Autowired
		private RESTAuthenticationEntryPoint authenticationEntryPoint;

		@Autowired
		private PasswordEncoder passwordEncoder;

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.httpBasic().and().authorizeRequests().antMatchers("/", "/assets/**", "/app/**", "/components/**", "/api/rest/registrations/**").permitAll()
					.anyRequest().authenticated().and().addFilterAfter(new AngularCsrfHeaderFilter(), CsrfFilter.class).csrf()
					.csrfTokenRepository(csrfTokenRepository()).and().csrf().disable().logout().logoutUrl("/logout").logoutSuccessUrl("/").and().headers()
					.frameOptions().sameOrigin().httpStrictTransportSecurity().disable();

			http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
		}

		private CsrfTokenRepository csrfTokenRepository() {
			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
			repository.setHeaderName("X-XSRF-TOKEN");
			return repository;
		}

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		}

	}

}