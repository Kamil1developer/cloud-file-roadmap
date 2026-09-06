package org.roadmap.security;

import lombok.RequiredArgsConstructor;

import org.roadmap.entity.User;
import org.roadmap.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService  implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));


        return org.springframework.security.core.userdetails.User.builder()
                .username(user.username)
                .password(user.passwordHash)
                .roles("USER")
                .build();
    }
}
