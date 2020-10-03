package cs203.g1t7.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userSvc){
        this.userDetailsService = userSvc;
    }
    
    /** 
     * Attach the user details and password encoder.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(encoder());
    }

    /**
     * 
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .httpBasic()
            .and() 
        .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/contents/**").authenticated() 
            .antMatchers(HttpMethod.POST, "/contents/**").authenticated() 
            .antMatchers(HttpMethod.PUT, "/contents/**").authenticated()
            .antMatchers(HttpMethod.DELETE, "/contents/**").authenticated()
            
            .antMatchers(HttpMethod.GET, "/contents/approved").hasRole("USER")
            .antMatchers(HttpMethod.GET, "/contents").hasAnyRole("MANAGER", "ANALYST")
            .antMatchers(HttpMethod.POST, "/contents").hasAnyRole("MANAGER", "ANALYST")
            .antMatchers(HttpMethod.PUT, "/contents/approved").hasRole("MANAGER")
            .antMatchers(HttpMethod.PUT, "/contents/title", "/contents/summary", "contents/content", "contents/link").hasAnyRole("MANAGER", "ANALYST")
            .antMatchers(HttpMethod.DELETE, "/contents/*").hasAnyRole("MANAGER", "ANALYST")
            .antMatchers(HttpMethod.PUT, "/contents/*").hasAnyRole("USER", "MANAGER", "ANALYST")
            .antMatchers(HttpMethod.GET, "/users").hasAnyRole("USER", "MANAGER", "ANALYST")
            .antMatchers(HttpMethod.POST, "/users").hasRole("MANAGER")
            .antMatchers(HttpMethod.PUT, "/users").hasRole("MANAGER")

            .and()
        .csrf().disable() // CSRF protection is needed only for browser based attacks
        .formLogin().disable()
        .headers().disable(); // Disable the security headers, as we do not return HTML in our service
    }

    /**
     * @Bean annotation is used to declare a PasswordEncoder bean in the Spring application context. 
     * Any calls to encoder() will then be intercepted to return the bean instance.
     */
    @Bean
    public BCryptPasswordEncoder encoder() {
        // auto-generate a random salt internally
        return new BCryptPasswordEncoder();
    }
}
 