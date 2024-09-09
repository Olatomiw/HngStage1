package com.example.stage1Hng.Security;

import com.example.stage1Hng.Repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        DefaultSecurityFilterChain defaultSecurityFilterChain = http
                .csrf(e-> e.disable())
                .authorizeHttpRequests(e-> e
                        .anyRequest().authenticated()
                        .requestMatchers(HttpMethod.GET).permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .build();

        return defaultSecurityFilterChain;
    }

    @Bean
    public UserDetailsService users(UserRepository userRepository){
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("password")
//                .roles("ADMIN")
//                .build();
//        UserDetails user = User.builder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
        return new CustomUserDetailsService();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
        return passwordEncoder;
    }
}
