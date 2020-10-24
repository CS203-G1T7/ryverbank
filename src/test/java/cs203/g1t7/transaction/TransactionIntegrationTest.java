package cs203.g1t7.transaction;

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
		// User user1 = new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true);
		// users.save(user1);
		// User user2 = new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S0298147E", "81935765", "Jalan Hoy 78 Mandala", true);
        // users.save(user2);

		// // ADD IN ACCOUNT FOR EXISTING USER
		// Account account = new Account(user1.getId(), 5000.0, 5000.0);
		// accounts.save(account);

		// List<Transaction> t_s = new ArrayList<Transaction>();

		// Account account2 = new Account(user2.getId(), 5000.0, 5000.0);
		// accounts.save(account2);
		
		// Transaction trans = new Transaction(account.getId(), account2.getId(), 1000.0);
		// trans.setAccount(account);
		// transactions.save(trans);

		// t_s.add(trans);
		// account.setTransactions(t_s);

		users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

		Account acc1 = new Account(1, 5000, 5000);
		Account acc2 = new Account(2, 5000, 5000);

		Transaction trans1 = new Transaction(1, 2, 5000);
		trans1.setAccount(acc1);

		List<Transaction> t1 = new ArrayList<Transaction>();
		t1.add(trans1);
		acc1.setTransactions(t1);

		accounts.save(acc1);

		URI uri = new URI(baseUrl + port + "/api/accounts/" + acc1.getId() + "/transactions");
        
        // Need to use array with a ReponseEntity here
		ResponseEntity<Transaction[]> result = restTemplate.withBasicAuth("user_1", "01_user_01").getForEntity(uri, Transaction[].class);
		Transaction[] transaction = result.getBody();

		assertEquals(200, result.getStatusCodeValue());
		// assertEquals(2, result.length);
	}

	// GET ACCOUNT FOR ROLE_USER w/ INVALID ID
	// @Test
	// public void getTransaction_AuthenticationInvalid_Failure() throws Exception {
    //     // ADD IN USER
    //     users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
    //     users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

	// 	// ADD IN ACCOUNT FOR EXISTING USER
	// 	Account acc1 = new Account(1, 5000, 5000);
    //     accounts.save(acc1);
    //     accounts.save(new Account(2, 5000, 5000));
        
	// 	URI uri = new URI(baseUrl + port + "/accounts/" + acc1.getId() + "/transactions");
		
	// 	// ResponseEntity<Transaction> result = restTemplate.withBasicAuth("user_2", "02_user_02").getForEntity(uri, Transaction.class);
	// 	ResponseEntity<Transaction[]> result = restTemplate.withBasicAuth("user_1", "01_user_01").getForEntity(uri, Transaction[].class);
	// 	Transaction[] transaction = result.getBody();
			
	// 	assertEquals(403, result.getStatusCode().value());
	// }

	@Test
  public void getTransaction_AuthenticationInvalid_Failure() throws Exception {
        // ADD IN USER
        User user_1 = users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        User user_2 = users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

    // ADD IN ACCOUNT FOR EXISTING USER
    Account acc1 = new Account(user_1.getId(), 5000, 5000);
    acc1 = accounts.save(acc1);
    accounts.save(new Account(user_2.getId(), 5000, 5000));
        
    URI uri = new URI(baseUrl + port + "/api/accounts/" + acc1.getId() + "/transactions");
    // System.out.println("uri = " + uri);

    // ResponseEntity<Void> result = restTemplate.withBasicAuth("user_2", "02_user_02").getForEntity(uri, void.class);

    ResponseEntity<Transaction[]> result = restTemplate.withBasicAuth("user_2", "02_user_02").getForEntity(uri, Transaction[].class);
    // ResponseEntity<Transaction[]> result = restTemplate.withBasicAuth("user_1", "01_user_01").getForEntity(uri, Transaction[].class);
    Transaction[] transaction = result.getBody();
      
    assertEquals(403, result.getStatusCode().value());
  }

	@Test
	public void getTransaction_InvalidId_Failure() throws Exception {
		User user = new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true);
		users.save(user);
		
		User user2 = new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true);
		users.save(user2);

		Account account = new Account(user.getId(), 5000.0, 5000.0);
		accounts.save(account);

		List<Transaction> t_s = new ArrayList<Transaction>();

		Account account2 = new Account(user2.getId(), 5000.0, 5000.0);
		accounts.save(account2);
		
		Transaction transaction = new Transaction(account.getId(), account2.getId(), 1000.0);
		transaction.setAccount(account);
		transactions.save(transaction);

		t_s.add(transaction);
		account.setTransactions(t_s);

        URI uri = new URI(baseUrl + port + "/api/accounts/" + account.getId() + "/transactions/2");

		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
		
		ResponseEntity<Transaction> result = restTemplate.withBasicAuth("admin", "goodpassword").getForEntity(uri, Transaction.class);
			
		assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void getTransaction_ValidAccountId_Success() throws Exception {
        users.save(new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "holahola", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));
        users.save(new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Stitch", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true));

		Account acc1 = new Account(1, 5000, 5000);
		Account acc2 = new Account(2, 5000, 5000);

		Transaction trans1 = new Transaction(1, 2, 5000);
		trans1.setAccount(acc1);

		List<Transaction> t1 = new ArrayList<Transaction>();
		t1.add(trans1);
		acc1.setTransactions(t1);

		accounts.save(acc1);

		URI uri = new URI(baseUrl + port + "/api/accounts/" + acc1.getId() + "/transactions/" + trans1.getId());

		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
		
		ResponseEntity<Transaction> result = restTemplate.withBasicAuth("admin", "goodpassword").getForEntity(uri, Transaction.class);
			
		assertEquals(200, result.getStatusCode().value());
		// assertEquals(content.getTitle(), result.getBody().getTitle());
	}

	@Test
	public void addTransaction_AuthenticationValid_Success() throws Exception {
        User user1 = new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "Alicia Wong", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true);
        User user2 = new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Lilo Stitch", "S4726114G", "81235769", "Jalan Hope 78 Durian", true);
        users.save(user1);
        users.save(user2);
        
        Account acc1 = new Account(user1.getId(), 5000, 5000);
        Account acc2 = new Account(user2.getId(), 5000, 5000);
        accounts.save(acc1);
		accounts.save(acc2);

		URI uri = new URI(baseUrl + port + "/api/accounts/" + acc1.getId() + "/transactions");

		Transaction trans1 = new Transaction(acc1.getId(), acc2.getId(), 5000);
		trans1.setAccount(acc1);

		List<Transaction> t1 = new ArrayList<Transaction>();
		t1.add(trans1);
		//acc1.setTransactions(t1);
        
        ResponseEntity<Transaction> result = restTemplate.withBasicAuth("user_1", "01_user_01")
										.postForEntity(uri, trans1, Transaction.class);
			
		assertEquals(201, result.getStatusCode().value());
		// assertEquals(content.getTitle(), result.getBody().getTitle());
	}

	@Test
	public void addTransaction_AuthenticationInvalid_Failure() throws Exception {
        User user1 = new User("user_1", encoder.encode("01_user_01"), "ROLE_USER", "Alicia Wong", "S9794462H", "81235765", "Jalan Durian 78 Mandala", true);
        User user2 = new User("user_2", encoder.encode("02_user_02"), "ROLE_USER", "Lilo Stitch", "S4726114G", "81235769", "Jalan Hope 78 Durian", true);
        User user3 = new User("user_3", encoder.encode("03_user_03"), "ROLE_USER", "Amanda Wang", "S7148301J", "81235768", "Jalan Besar 11892", true);

        users.save(user1);
        users.save(user2);
        users.save(user3);
        
        Account acc1 = new Account(user1.getId(), 5000, 5000);
        Account acc2 = new Account(user2.getId(), 5000, 5000);
        accounts.save(acc1);
		accounts.save(acc2);

		URI uri = new URI(baseUrl + port + "/api/accounts/" + acc1.getId() + "/transactions");

		Transaction trans1 = new Transaction(acc1.getId(), acc2.getId(), 5000);
		trans1.setAccount(acc1);

		List<Transaction> t1 = new ArrayList<Transaction>();
		t1.add(trans1);

        ResponseEntity<Transaction> result = restTemplate.withBasicAuth("user_3", "03_user_03")
										.postForEntity(uri, trans1, Transaction.class);
			
		assertEquals(403, result.getStatusCode().value());
	}

}
