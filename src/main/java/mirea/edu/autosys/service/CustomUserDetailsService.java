package mirea.edu.autosys.service;

import mirea.edu.autosys.model.User;
import mirea.edu.autosys.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getName())
                .password(user.getPassword())
                // .authorities(user.getRoles().stream()
                //         .map(role -> role.getAuthority())
                //         .toArray(String[]::new))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                //.disabled(!user.isEnabled())
                .build();
    }
}

