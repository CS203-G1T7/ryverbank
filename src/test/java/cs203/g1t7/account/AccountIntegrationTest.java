package cs203.g1t7.account;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import cs203.g1t7.users.User;
import cs203.g1t7.users.UserRepository;

import cs203.g1t7.transaction.*;

/** Start an actual HTTP server listening at a random port*/
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AccountIntegrationTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	/**
	 * Use TestRestTemplate for testing a real instance of your application as an external actor.
	 * TestRestTemplate is just a convenient subclass of RestTemplate that is suitable for integration tests.
 	 * It is fault tolerant, and optionally can carry Basic authentication headers.
	 */
	private TestRestTemplate restTemplate;

	@Autowired
	private AccountRepository accounts;

	@Autowired
	private UserRepository users;

	@Autowired
	private BCryptPasswordEncoder encoder;


	@AfterEach
	void tearDown(){
		// clear the database after each test
		accounts.deleteAll();
		users.deleteAll();
	}

	// GET ALL ACCOUNTS FOR ROLE_MANAGER
	@Test
	public void getAccount_AuthenticationValid_Success() throws Exception {
        // ADD IN USER
		users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        // ADD IN ACCOUNT FOR EXISTING USER
        accounts.save(new Account(1, 5000, 5000));

		URI uri = new URI(baseUrl + port + "/api/accounts");
        
        //ADD IN MANAGER
		users.save(new User("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
        
        // Need to use array with a ReponseEntity here
		ResponseEntity<Account[]> result = restTemplate.withBasicAuth("manager_1", "01_manager_01").getForEntity(uri, Account[].class);
		Account[] accounts = result.getBody();

		assertEquals(200, result.getStatusCodeValue());
		assertEquals(1, accounts.length);
	}

	// GET ACCOUNT FOR ROLE_USER w/ INVALID ID
	@Test
	public void getAccount_AuthenticationInvalid_Failure() throws Exception {
        // ADD IN USER
        User user1 = users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        User user2 = users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

        // ADD IN ACCOUNT FOR EXISTING USER
        Account acc1 = accounts.save(new Account(1, 5000, 5000));
        Account acc2 = accounts.save(new Account(2, 5000, 5000));
        
		URI uri = new URI(baseUrl + port + "/api/accounts/" + acc1.getId());
		
		ResponseEntity<Account> result = restTemplate.withBasicAuth("user_2", "02_user_02").getForEntity(uri, Account.class);
			
		assertEquals(403, result.getStatusCode().value());
	}

	@Test
	public void getAccount_InvalidId_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/accounts/1");
        User user1 = users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
		
		ResponseEntity<Account> result = restTemplate.withBasicAuth("user_1", "01_user_01").getForEntity(uri, Account.class);
			
		assertEquals(400, result.getStatusCode().value());
	}

	@Test
	public void getAccount_ValidAccountId_Success() throws Exception {
        User user = users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        Account account = accounts.save(new Account(user.getId(), 5000, 5000));

		URI uri = new URI(baseUrl + port + "/api/accounts/" + account.getId());
		
		ResponseEntity<Account> result = restTemplate.withBasicAuth("user_1", "01_user_01").getForEntity(uri, Account.class);
			
		assertEquals(200, result.getStatusCode().value());
	}

	@Test
	public void addAccount_AuthenticationValid_Success() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/accounts");
		User user = users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S1932701I", "81235768", "Jalan Cilandak 78 Mandala", true));
        
		Account account = new Account(user.getId(), 50000.0, 40000.0);
        
        ResponseEntity<Account> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.postForEntity(uri, account, Account.class);
			
		assertEquals(201, result.getStatusCode().value());
		// assertEquals(content.getTitle(), result.getBody().getTitle());
	}

	@Test
	public void addAccount_AuthenticationInvalid_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/accounts");
        users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

        Account account = new Account(1, 50000, 40000);

        ResponseEntity<Account> result = restTemplate.withBasicAuth("user_2", "02_user_02")
										.postForEntity(uri, account, Account.class);
			
		assertEquals(403, result.getStatusCode().value());
	}

}
