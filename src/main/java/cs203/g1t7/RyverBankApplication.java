package cs203.g1t7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cs203.g1t7.client.RestTemplateClient;
import cs203.g1t7.users.User;
import cs203.g1t7.users.UserRepository;
import cs203.g1t7.content.Content;
import cs203.g1t7.content.ContentRepository;

@SpringBootApplication
public class RyverBankApplication {

	public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(RyverBankApplication.class, args);

        // JPA user repository init
        UserRepository users = ctx.getBean(UserRepository.class);
        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
        System.out.println("[Add user]: " + users.save(
            new User("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER", "Raymond Tan", "S5118309F", "91251234", "27 Jalan Hidup S680234", true)));
        System.out.println("[Add user]: " + users.save(
            new User("analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST", "Abang John", "S7251849G", "82345678", "27 Bukit Timah S123456", true)));
        System.out.println("[Add user]: " + users.save(
            new User("analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST", "Alice Wong", "S5558664J", "92345678", "127 Bukit Batok", true)));
    }
    
}
