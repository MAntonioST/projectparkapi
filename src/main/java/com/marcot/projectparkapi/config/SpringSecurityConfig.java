package com.marcot.projectparkapi.config;

import com.marcot.projectparkapi.entity.UserAccount;
import com.marcot.projectparkapi.jwt.JwtAuthorizationFilter;
import com.marcot.projectparkapi.repository.UserAccountRepository;
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

@EnableMethodSecurity
@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

    @Autowired
    private UserAccountRepository userEntityRepository;

    private static final String[] DOCUMENTATION_OPENAPI = {
            "/docs/index.html",
            "/docs-park.html", "/docs-park/**",
            "/v3/api-docs/**",
            "/swagger-ui-custom.html", "/swagger-ui.html", "/swagger-ui/**",
            "/**.html", "/webjars/**", "/configuration/**", "/swagger-resources/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "api/v1/users").access((authentication, context) -> {
                            Authentication authen = authentication.get();
                            if (authen == null || !authen.isAuthenticated()) {
                                return new AuthorizationDecision(true);
                            }
                            return new AuthorizationDecision(!hasRole(authen, "ADMIN"));
                        })
                        .requestMatchers(HttpMethod.POST, "api/v1/customers").access((authentication, context) -> {
                            Authentication authen = authentication.get();
                            return new AuthorizationDecision(!hasRole(authen, "ADMIN"));
                        })
                        .requestMatchers(HttpMethod.GET, "api/v1/customers").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "api/v1/parking-spaces").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/v1/parking-spaces").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/v1/parking-spaces/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "api/v1/parking-spaces/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "api/v1/parking-spaces/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "api/v1/parking/check-in").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/v1/parking/check-in/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "api/v1/parking/check-out/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/v1/parking/check-in/cpf/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/v1/customers/details").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "api/v1/auth").permitAll()
                        .requestMatchers(DOCUMENTATION_OPENAPI).permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/v1/users/{id}")
                        .access(createAuthorizationManager("ADMIN", true))
                        .requestMatchers(HttpMethod.PATCH, "api/v1/users/{id}")
                        .access(createAuthorizationManager("ADMIN", true))
                        .requestMatchers(HttpMethod.DELETE, "api/v1/users/{id}")
                        .access(createAuthorizationManager("ADMIN", true))
                        .requestMatchers(HttpMethod.GET, "api/v1/customers/{id}")
                        .access(createAuthorizationManager("ADMIN", false))
                        .requestMatchers(HttpMethod.GET, "api/v1/customers/{id}")
                        .access(createAuthorizationManager("CLIENTE", false))
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private AuthorizationManager<RequestAuthorizationContext> createAuthorizationManager(String role, boolean adminAccessAllowed) {
        return (authentication, context) -> {
            Authentication auth = authentication.get();
            if (hasRole(auth, role)) {
                return new AuthorizationDecision(true);
            }
            if (adminAccessAllowed && hasRole(auth, "ADMIN")) {
                return new AuthorizationDecision(true);
            }
            Long userId = Long.parseLong(context.getVariables().get("id"));
            return new AuthorizationDecision(hasUserId(auth, userId));
        };
    }

    private boolean hasUserId(Authentication authentication, Long userId) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserAccount user = userEntityRepository.findByUsername(authentication.getName()).orElse(null);
            return user != null && user.getId().equals(userId);
        }
        return false;
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
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