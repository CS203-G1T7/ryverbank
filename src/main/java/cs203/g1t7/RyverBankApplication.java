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

        // JPA content repository init
        ContentRepository contents = ctx.getBean(ContentRepository.class);
        System.out.println("[Add content]: " + contents.save(new Content("Spring Security Fundamentals", "summary", "content", "link1234567890", true)).getTitle());
        System.out.println("[Add content]: " + contents.save(new Content("Gone With The Wind", "summary", "content", "link12345", true)).getTitle());

        // JPA user repository init
        UserRepository users = ctx.getBean(UserRepository.class);
        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
        System.out.println("[Add user]: " + users.save(
            new User("marktan123", encoder.encode("goodpassword"), "ROLE_USER", "marktan", "S5118309F", "91251234", "27 Jalan Alamak S680234", true)));
        System.out.println("[Add user]: " + users.save(
            new User("johntan123", encoder.encode("notgoodpassword"), "ROLE_ANALYST", "johntan", "S7251849G", "82345678", "27 Bukit Timah S123456", true)));
        System.out.println("[Add user]: " + users.save(
            new User("miketan123", encoder.encode("password"), "ROLE_MANAGER", "miketan", "S2036435A", "62345667", "1 Thomson Road S987654", true)));
    }
    
}
