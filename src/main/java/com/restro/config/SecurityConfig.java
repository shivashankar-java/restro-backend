package com.restro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {

		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> {})

				.addFilterBefore(
						new JwtFilter(jwtUtil),
						UsernamePasswordAuthenticationFilter.class
				)

				.authorizeHttpRequests(auth -> auth

						// Public APIs
						.requestMatchers("/auth/**").permitAll()

						// Swagger
						.requestMatchers(
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/swagger-ui.html"
						).permitAll()

						// Admin only APIs
						.requestMatchers("/admin/**").hasRole("ADMIN")

						// Customer + Admin APIs
						.requestMatchers("/menu/**").hasAnyRole("ADMIN", "CUSTOMER")
						.requestMatchers("/api/cart/**").hasRole("CUSTOMER")
						.requestMatchers("/api/orders/**").hasRole("CUSTOMER")

						// COUPONS
						.requestMatchers(HttpMethod.POST, "/api/coupons/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/coupons/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/coupons/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/coupons/**").hasAnyRole("ADMIN", "CUSTOMER")

						.requestMatchers(HttpMethod.POST, "/api/coupons/apply").hasRole("CUSTOMER")

						// Restaurant APIs
						// POST -> ADMIN only
						.requestMatchers(HttpMethod.POST, "/api/restaurants", "/api/restaurants/**")
						.hasRole("ADMIN")

						.requestMatchers(HttpMethod.GET, "/api/restaurants", "/api/restaurants/**")
						.hasAnyRole("ADMIN", "CUSTOMER")

						// Remaining secured
						.anyRequest().authenticated()
				);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
