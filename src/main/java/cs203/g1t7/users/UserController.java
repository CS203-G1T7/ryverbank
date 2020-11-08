package cs203.g1t7.users;

import java.util.List;
import java.util.ArrayList;

import javax.validation.Valid;

import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.core.context.SecurityContextHolder;

import cs203.g1t7.asset.AssetController;

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

    @GetMapping("/api/customers")
    public List<User> getUsers() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // user can only see his/her own profile, return 403 when user tries to see list of customers
        if(user.getAuthority().equals("ROLE_USER")) throw new UserForbiddenException();
        return users.findAll();
    }

    @GetMapping("/api/customers/{id}")
    public User getUser(@PathVariable Integer id) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User target = users.findById(id).get();        
        if(target == null) {
            throw new UserNotFoundException(id);
        }
        // there is no user with such username
        if(!users.findByUsername(user.getUsername()).isPresent()) throw new UserNotAuthenticatedException(user.getUsername());
        // return customer only if it's active and matches the requester id
        if(user.getAuthority().equals("ROLE_USER") || user.getAuthority().equals("ROLE_ANALYST")) {
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
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/customers")
    public User addUser(@Valid @RequestBody User user) {
        // check whether username is used or not
        if(users.findByUsername(user.getUsername()).isPresent()) throw new UsernameExistsException(user.getUsername());
        // check valid NRIC
        NricValidation validate = new NricValidation();
        String nric = user.getNric();
        if (!validate.validateNric(nric)) {
            throw new NotValidNricException(nric);
        }
        // set password to be encoded
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }

    @PutMapping("/api/customers/{id}")
    public User updateUser(@PathVariable (value = "id") Integer id, @Valid @RequestBody User newUserInfo) {
        if(id == null) throw new UserNotFoundException(id);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // user to compare with to check whether the user in database is the same as the requester
        User compare = users.findById(id).get();
        // ROLE_USER can only update phone, password and address, others are ignored
        if(user.getAuthority().equals("ROLE_USER") && (user.equals(compare))) {
            return users.findById(id).map(customer -> {customer.setPhone(newUserInfo.getPhone());
                                                        customer.setPassword(encoder.encode(newUserInfo.getPassword())); 
                                                        customer.setAddress(newUserInfo.getAddress());
            return users.save(customer);
            }).orElse(null);
        }
        // ROLE_MANAGER can update everything
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
