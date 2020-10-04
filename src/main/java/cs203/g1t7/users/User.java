package cs203.g1t7.users;

import java.util.Arrays;
import java.util.Collection;
import cs203.g1t7.users.NricValidation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

/* Implementations of UserDetails to provide user information to Spring Security, 
e.g., what authorities (roles) are granted to the user and whether the account is enabled or not
*/
public class User implements UserDetails{
    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    
    @NotNull(message = "Username should not be null")
    private String username;
    
    @NotNull(message = "Password should not be null")
    private String password;

    @NotNull(message = "Authorities should not be null")
    // We define three roles/authorities: ROLE_USER or ROLE_MANAGER or ROLE_ANALYST
    private String authorities;

    @NotNull(message = "Full name should not be null")
    @Pattern(regexp = "^[a-zA-Z ]*$", message="Full name should only contain alphabetical letters")
    private String full_name;

    @NotNull(message = "NRIC should not be null")
    @Size(min = 9, max = 9, message = "NRIC should contain 9 characters")
    private String nric;

    @NotNull(message = "Phone number should not be null")
    @Size(min = 8, max = 8, message = "Phone number should contain 8 numbers")
    @Pattern(regexp = "^[689][0-9]+$", message="Only SG phone numbers are supported")
    private String phone;

    @NotNull(message = "Address should not be null")
    private String address;

    @NotNull(message = "Status should not be null")
    private boolean active;

    public User(String username, String password, String authorities, String full_name, String nric, String phone, String address, boolean active){
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.full_name = full_name;
        this.nric = nric;
        this.phone = phone;
        this.address = address;
        this.active = active;
    }


    /* Return a collection of authorities (roles) granted to the user.
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(authorities));
    }

    public String getAuthority() {
        return this.authorities;
    }

    //@Override
    public boolean getActive() {
        return this.active;
    }

    /*
    The various is___Expired() methods return a boolean to indicate whether
    or not the user’s account is enabled or expired.
    */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

    // @Override
    // public String getUsername() {
    //     return this.username;
    // }

}