package cs203.g1t7.users;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository users;
    
    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }
    @Override
    /** To return a UserDetails for Spring Security 
     *  Note that the method takes only a username.
        The UserDetails interface has methods to get the password.
    */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }
    
}