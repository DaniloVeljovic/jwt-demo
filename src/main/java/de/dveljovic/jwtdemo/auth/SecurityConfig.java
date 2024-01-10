package de.dveljovic.jwtdemo.auth;

import de.dveljovic.jwtdemo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(final CustomUserDetailsService customUserDetailsService,
                          final JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    @SuppressWarnings("deprecation")
    public NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(final HttpSecurity httpSecurity, final NoOpPasswordEncoder noOpPasswordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(noOpPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/rest/auth/**").permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
