package cs203.g1t7.users;

import java.util.List;

import javax.validation.Valid;

import java.util.regex.Pattern;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
public class UserController {
    private UserRepository users;
    private CustomUserDetailsService cuds = new CustomUserDetailsService(users);
    private BCryptPasswordEncoder encoder;
    private UserDetailsManager udm;

    public UserController(UserRepository users, BCryptPasswordEncoder encoder){
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping("/customers")
    public List<User> getUsers() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getAuthority().equals("ROLE_USER")) throw new UserForbiddenException();
        return users.findAll();
    }

    @GetMapping("/customers/{id}")
    public User getUser(@PathVariable Integer id) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User target = users.findById(id).get();
        if(target == null) {
            throw new UserNotFoundException(id);
        }
        if(user.getAuthority().equals("ROLE_USER")) {
            if(user.getActive() == false) {
                throw new UserDeactivatedException();
            }
            if(user.getId() != target.getId()) throw new UserForbiddenException();
        }
        return target;
    }

    /**
    * Using BCrypt encoder to encrypt the password for storage 
    * @param user
     * @return
     */
    @PostMapping("/customers")
    public User addUser(@Valid @RequestBody User user) {
        NricValidation validate = new NricValidation();
        String nric = user.getNric();
        if (!validate.validateNric(nric)) {
            throw new NotValidNricException(nric);
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }

    @PutMapping("/customers/{id}")
    public User updateUser(@PathVariable (value = "id") Integer id, @Valid @RequestBody User newUserInfo) {
        if(id == null) throw new UserNotFoundException(id);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getAuthority().equals("ROLE_USER")) {
            return users.findById(id).map(customer -> {customer.setPhone(newUserInfo.getPhone());
                                                        customer.setPassword(encoder.encode(newUserInfo.getPassword())); 
                                                        customer.setAddress(newUserInfo.getAddress());
            return users.save(customer);
            }).orElse(null);
        }
        if(user.getAuthority().equals("ROLE_MANAGER")) {
            return users.findById(id).map(customer -> {customer.setPhone(newUserInfo.getPhone());
                            customer.setFull_name(newUserInfo.getFull_name());
                            customer.setNric(newUserInfo.getNric());
                            customer.setUsername(newUserInfo.getUsername());
                            customer.setAuthorities(newUserInfo.getAuthority());
                            customer.setActive(newUserInfo.getActive());
                            customer.setPassword(encoder.encode(newUserInfo.getPassword())); 
                            customer.setAddress(newUserInfo.getAddress());
            return users.save(customer);
            }).orElse(null);
        }
            return users.findById(id).get();
        }
}