/*******************************************************************************
Copyright (c) 2016 Eviarc GmbH.
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

import java.util.Optional;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cloud.aws.context.config.annotation.EnableContextRegion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import dash.security.AngularCsrfHeaderFilter;
import dash.security.License;
import dash.security.LicenseFilter;
import dash.security.listener.RESTAuthenticationEntryPoint;
import dash.smtpmanagement.business.SmtpService;
import dash.smtpmanagement.domain.Encryption;
import dash.smtpmanagement.domain.Smtp;
import dash.usermanagement.business.UserService;
import dash.usermanagement.domain.Role;
import dash.usermanagement.domain.User;
import dash.usermanagement.registration.domain.Validation;
import dash.usermanagement.settings.language.Language;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableJpaRepositories
@EnableContextRegion(region = "eu-central-1")
public class Application {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	@EnableSwagger2
	public static class SwaggerConfig {
		@Bean
		public Docket api() {
			return new Docket(DocumentationType.SWAGGER_2).select()
					.apis(RequestHandlerSelectors.basePackage("dash.publicapi")).paths(PathSelectors.any()).build()
					.apiInfo(apiInfo());

		}
	}

	private static ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Lead+").description("Lead+ - Lead Management Tool").license("")
				.licenseUrl("").version("2.0").build();
	}

	@Autowired
	private UserService userService;

	@Autowired
	private SmtpService smtpService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Validation validation() {
		return new Validation();
	}

	@PostConstruct
	public void createAdminIfNotExists() throws Exception {

		// if
		// (!Optional.ofNullable(userService.getUserByName("admin")).isPresent())
		// {
		// User admin = new User();
		//
		// admin.setUsername("admin".toLowerCase());
		// admin.setPassword(passwordEncoder().encode("admin"));
		// admin.setFirstname("firstAdmin");
		// admin.setLastname("lastAdmin");
		// admin.setEmail("admin@eviarc.com");
		// admin.setRole(Role.ADMIN);
		// admin.setEnabled(true);
		// admin.setLanguage(Language.DE);
		//
		// userService.save(admin);
		// }

		if (!Optional.ofNullable(userService.getUserByName("andreas.foitzik")).isPresent()) {
			User test = new User();

			test.setUsername("andreas.foitzik".toLowerCase());
			test.setPassword(passwordEncoder().encode("test"));
			test.setEmail("andreas.foitzik@get-net.eu");
			test.setFirstname("Andreas");
			test.setLastname("Foitzik");
			test.setRole(Role.SUPERADMIN);
			test.setEnabled(true);
			test.setLanguage(Language.DE);

			userService.save(test);

			Smtp testSmtp = new Smtp();
			testSmtp.setConnection(false);
			testSmtp.setEmail("andreas.foitzik@get-net.eu");
			testSmtp.setEncryption(Encryption.TLS);
			testSmtp.setHost("alfa3017.alfahosting-server.de");
			testSmtp.setPassword("***REMOVED***".getBytes("UTF-8"));
			testSmtp.setPort(25);
			testSmtp.setResponseAdress("");
			testSmtp.setSender("Andreas Foitzik");
			testSmtp.setUsername("web26262457p2");
			testSmtp.setUser(test);

			smtpService.save(testSmtp);
		}

		// if
		// (!Optional.ofNullable(userService.getUserByName("testUser")).isPresent())
		// {
		// User test = new User();
		//
		// test.setUsername("testUser".toLowerCase());
		// test.setPassword(passwordEncoder().encode("testUser"));
		// test.setFirstname("firstTestUser");
		// test.setLastname("lastTestUser");
		// test.setEmail("testUser@eviarc.com");
		// test.setRole(Role.USER);
		// test.setEnabled(true);
		// test.setLanguage(Language.DE);
		//
		// userService.save(test);
		// }

		if (!Optional.ofNullable(userService.getUserByName("api")).isPresent()) {
			User apiuser = new User();

			apiuser.setUsername("api".toLowerCase());
			apiuser.setPassword(passwordEncoder().encode("!APQYtDwgBtNqNY5L"));
			apiuser.setFirstname("firstApi");
			apiuser.setLastname("lastApi");
			apiuser.setEmail("api@eviarc.com");
			apiuser.setRole(Role.API);
			apiuser.setEnabled(true);
			apiuser.setLanguage(Language.DE);

			userService.save(apiuser);
		}

		// if
		// (!Optional.ofNullable(userService.getUserByName("test")).isPresent())
		// {
		// User test = new User();
		//
		// test.setUsername("test".toLowerCase());
		// test.setPassword(passwordEncoder().encode("test"));
		// test.setFirstname("firstTest");
		// test.setLastname("lastTest");
		// test.setEmail("test@eviarc.com");
		// test.setRole(Role.SUPERADMIN);
		// test.setEnabled(true);
		// test.setLanguage(Language.DE);
		//
		// userService.save(test);
		// }
	}

	@Configuration
	@EnableWebSecurity
	@EnableGlobalMethodSecurity(prePostEnabled = true)
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
			http.httpBasic().and().authorizeRequests()
					.antMatchers("/", "/images/favicon**", "/assets/**", "/fonts/**", "/app/**",
							"/components/Login/view/Login.html", "/components/Signup/view/Signup.html",
							"/api/rest/registrations/**", "/swagger-ui.html", "/webjars/springfox-swagger-ui/**",
							"/configuration/ui", "/swagger-resources", "/v2/api-docs/**", "/configuration/security")
					.permitAll().antMatchers("/api/rest/public**").hasAnyAuthority("SUPERADMIN,ADMIN,USER,API")
					.anyRequest().authenticated().antMatchers("/**").hasAnyAuthority("SUPERADMIN,ADMIN,USER")
					.antMatchers(License.BASIC.getBlockedRoutes()
							.toArray(new String[License.BASIC.getBlockedRoutes().size()]))
					.authenticated().and().addFilterBefore(new LicenseFilter(), FilterSecurityInterceptor.class)
					.authorizeRequests().anyRequest().authenticated().and()
					.addFilterAfter(new AngularCsrfHeaderFilter(), CsrfFilter.class).csrf()
					.csrfTokenRepository(csrfTokenRepository()).and().csrf().disable().logout().logoutUrl("/logout")
					.logoutSuccessUrl("/").and().headers().frameOptions().sameOrigin().httpStrictTransportSecurity()
					.disable();

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