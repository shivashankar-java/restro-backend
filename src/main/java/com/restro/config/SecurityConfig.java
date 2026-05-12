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

						//  Public APIs
						.requestMatchers("/auth/**", "/api/menu/**").permitAll()

						//  Swagger
						.requestMatchers(
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/swagger-ui.html"
						).permitAll()

						//  ADMIN only
						.requestMatchers("/admin/**").hasRole("ADMIN")

						//  CUSTOMER
						.requestMatchers("/api/cart/**").hasRole("CUSTOMER")
						.requestMatchers("/api/orders/**").hasRole("CUSTOMER")

						//  COUPONS
						.requestMatchers(HttpMethod.POST, "/api/coupons/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/coupons/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/coupons/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/coupons/**")
						.hasAnyRole("ADMIN", "CUSTOMER")

						.requestMatchers(HttpMethod.POST, "/api/coupons/apply")
						.hasRole("CUSTOMER")

						//  RESTAURANTS
						.requestMatchers(HttpMethod.POST, "/api/restaurants/**")
						.hasRole("ADMIN")

						.requestMatchers(HttpMethod.GET, "/api/restaurants/**")
						.hasAnyRole("ADMIN", "CUSTOMER")

						//  RESTAURANT OWNER APIs
						.requestMatchers("/api/restaurant/**")
						.hasRole("RESTAURANT_OWNER")

						// Example:
						// /api/restaurant/orders
						// /api/restaurant/menu

						// MENU MANAGEMENT

						.requestMatchers(HttpMethod.POST, "/api/menu", "/api/menu/**")
						.hasAnyRole("ADMIN", "RESTAURANT_OWNER")

						.requestMatchers(HttpMethod.PUT, "/api/menu/**")
						.hasAnyRole("ADMIN", "RESTAURANT_OWNER")

						.requestMatchers(HttpMethod.DELETE, "/api/menu/**")
						.hasAnyRole("ADMIN", "RESTAURANT_OWNER")

						//  DELIVERY PARTNER APIs
						.requestMatchers("/api/delivery/**")
						.hasRole("DELIVERY_PARTNER")

						// Example:
						// /api/delivery/orders
						// /api/delivery/accept

						//  Shared access (optional)
						.requestMatchers("/api/menu/manage/**")
						.hasAnyRole("ADMIN", "RESTAURANT_OWNER")

						//  All others
						.anyRequest().authenticated()
				);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
