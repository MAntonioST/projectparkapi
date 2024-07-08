package com.marcot.projectparkapi.config;


import com.marcot.projectparkapi.entity.UserEntity;
import com.marcot.projectparkapi.jwt.JwtAuthenticationEntryPoint;
import com.marcot.projectparkapi.jwt.JwtAuthorizationFilter;
import com.marcot.projectparkapi.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.function.Supplier;

@EnableMethodSecurity
@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "api/v1/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/v1/auth").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/v1/users/{id}")
                        .access(createAuthorizationManager("ADMIN", true))
                        .requestMatchers(HttpMethod.PUT, "api/v1/users/{id}")
                        .access(createAuthorizationManager("ADMIN", true))
                        .requestMatchers(HttpMethod.DELETE, "api/v1/users/{id}")
                        .access(createAuthorizationManager("ADMIN", true))
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .build();
    }

    private AuthorizationManager<RequestAuthorizationContext> createAuthorizationManager(String role, boolean adminAccessAllowed) {
        return (authentication, context) -> {
            if (hasRole(authentication, role)) {
                return new AuthorizationDecision(true);
            }
            if (adminAccessAllowed && hasRole(authentication, "ADMIN")) {
                return new AuthorizationDecision(true);
            }
            Long userId = Long.parseLong(context.getVariables().get("id"));
            return new AuthorizationDecision(hasUserId(authentication, userId));
        };
    }

    private boolean hasUserId(Supplier<Authentication> authentication, Long userId) {
        Authentication auth = authentication.get();
        if (auth != null && auth.isAuthenticated()) {
            UserEntity user = userEntityRepository.findByUsername(auth.getName()).orElse(null);
            return user != null && user.getId().equals(userId);
        }
        return false;
    }

    private boolean hasRole(Supplier<Authentication> authentication, String role) {
        Authentication auth = authentication.get();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }


    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}