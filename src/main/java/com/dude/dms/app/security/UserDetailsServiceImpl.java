package com.dude.dms.app.security;

import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implements the {@link UserDetailsService}.
 * This implementation searches for {@link User} entities by the login supplied in the login screen.
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Recovers the {@link User} from the database using the e-mail address supplied
     * in the login screen. If the user is found, returns a
     * {@link org.springframework.security.core.userdetails.User}.
     *
     * @param s User's login
     */
    @Override
    public UserDetails loadUserByUsername(String s) {
        User user = userRepository.findByLogin(s).orElse(null);
        if (null == user) {
            throw new UsernameNotFoundException("No user present with username: " + s);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPasswordHash(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
        }
    }
}