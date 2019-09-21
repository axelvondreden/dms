package com.dude.dms.spring.app.security;

import com.dude.dms.spring.backend.data.entity.User;
import com.dude.dms.spring.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * Implements the {@link UserDetailsService}.
 * This implementation searches for {@link User} entities by the e-mail address supplied in the login screen.
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
   * Recovers the {@link User} from the database using the name supplied in the login screen. If the user is found, returns a {@link org.springframework.security.core.userdetails.User}.
   *
   * @param username Username
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByName(username);
    if (user.isPresent()) {
      return new org.springframework.security.core.userdetails.User(user.get().getName(), "0x", Collections.singletonList(new SimpleGrantedAuthority("ADMIN")));
    } else {
      throw new UsernameNotFoundException("No user present with username: " + username);
    }
  }
}