package cs203.g1t7.transaction;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.Optional;

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
import cs203.g1t7.account.*;

/** Start an actual HTTP server listening at a random port*/
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TransactionIntegrationTest {

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
    
    @Autowired
    private TransactionRepository transactions;


	@AfterEach
	void tearDown(){
        // clear the database after each test
        accounts.deleteAll();
		transactions.deleteAll();
		users.deleteAll();
	}

	// GET ALL ACCOUNTS FOR ROLE_MANAGER
	@Test
	public void getTransactions_AuthenticationValid_Success() throws Exception {
        // ADD IN USER
        users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

        // ADD IN ACCOUNT FOR EXISTING USER
        accounts.save(new Account(1, 5000, 5000));
        accounts.save(new Account(2, 5000, 5000));

        transactions.save(new Transaction(1, 2, 5000));
        transactions.save(new Transaction(1, 2, 2000));


		URI uri = new URI(baseUrl + port + "/accounts/1/transactions");
        
        //ADD IN MANAGER
		users.save(new User("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
        
        // Need to use array with a ReponseEntity here
		ResponseEntity<Transaction[]> result = restTemplate.withBasicAuth("manager_1", "01_manager_01").getForEntity(uri, Transaction[].class);
		Transaction[] transaction = result.getBody();

		assertEquals(200, result.getStatusCodeValue());
		// assertEquals(2, result.length);
	}

	// GET ACCOUNT FOR ROLE_USER w/ INVALID ID
	@Test
	public void getTransaction_AuthenticationInvalid_Failure() throws Exception {
        // ADD IN USER
        users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

        // ADD IN ACCOUNT FOR EXISTING USER
        accounts.save(new Account(1, 5000, 5000));
        accounts.save(new Account(2, 5000, 5000));
        
		URI uri = new URI(baseUrl + port + "/accounts/1/transactions");
		
		ResponseEntity<Account> result = restTemplate.withBasicAuth("user_2", "02_user_02").getForEntity(uri, Account.class);
			
		assertEquals(403, result.getStatusCode().value());
	}

	@Test
	public void getTransaction_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/accounts/1/transaction/1");
        users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        accounts.save(new Account(1, 5000, 5000));

		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
		
		ResponseEntity<Transaction> result = restTemplate.withBasicAuth("admin", "goodpassword").getForEntity(uri, Transaction.class);
			
		assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void getTransaction_ValidAccountId_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/accounts/1/transaction/1");
        users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

        accounts.save(new Account(1, 5000, 5000));
        transactions.save(new Transaction(1, 2, 2000));

		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
		
		ResponseEntity<Transaction> result = restTemplate.withBasicAuth("admin", "goodpassword").getForEntity(uri, Transaction.class);
			
		assertEquals(200, result.getStatusCode().value());
		// assertEquals(content.getTitle(), result.getBody().getTitle());
	}

	@Test
	public void addTransaction_AuthenticationValid_Success() throws Exception {
		URI uri = new URI(baseUrl + port + "/accounts");
        users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
        
        Transaction transaction = new Transaction(1, 2, 3000);

        ResponseEntity<Transaction> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.postForEntity(uri, transaction, Transaction.class);
			
		assertEquals(201, result.getStatusCode().value());
		// assertEquals(content.getTitle(), result.getBody().getTitle());
	}

	@Test
	public void addTransaction_AuthenticationInvalid_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/accounts");
        users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        users.save(new User("user_3", encoder.encode("03_user_03"), "ROLE_USER", "Lilo", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

        Transaction transaction = new Transaction(1, 2, 3000);

        ResponseEntity<Transaction> result = restTemplate.withBasicAuth("user_3", "03_user_03")
										.postForEntity(uri, transaction, Transaction.class);
			
		assertEquals(403, result.getStatusCode().value());
	}

}
