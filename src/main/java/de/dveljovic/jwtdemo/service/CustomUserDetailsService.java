package de.dveljovic.jwtdemo.service;

import de.dveljovic.jwtdemo.model.User;
import de.dveljovic.jwtdemo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = userRepository.findUserByEmail(email);
        final List<String> roles = List.of("USER");
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.email())
                .password(user.password())
                .roles(roles.toArray(new String[0]))
                .build();
    }
}
