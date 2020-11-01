package cs203.g1t7.content;

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

/** Start an actual HTTP server listening at a random port*/
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ContentIntegrationTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "https://localhost:";

	@Autowired
	/**
	 * Use TestRestTemplate for testing a real instance of your application as an external actor.
	 * TestRestTemplate is just a convenient subclass of RestTemplate that is suitable for integration tests.
 	 * It is fault tolerant, and optionally can carry Basic authentication headers.
	 */
	private TestRestTemplate restTemplate;

	@Autowired
	private ContentRepository contents;

	@Autowired
	private UserRepository users;

	@Autowired
	private BCryptPasswordEncoder encoder;


	@AfterEach
	void tearDown(){
		// clear the database after each test
		contents.deleteAll();
		users.deleteAll();
	}

	// GET CONTENT FOR ROLE_MANAGER AND ROLE_ANALYST
	@Test
	public void getContent_AuthenticationValid_Success() throws Exception {
		Content content = new Content("Spring Security Fundamentals", "summary", "content", "link1234567890", false);
		contents.save(content);
		URI uri = new URI(baseUrl + port + "/api/contents");
		
		users.save(new User("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
		// Need to use array with a ReponseEntity here
		ResponseEntity<Content[]> result = restTemplate.withBasicAuth("manager_1", "01_manager_01").getForEntity(uri, Content[].class);
		Content[] contents = result.getBody();

		assertEquals(200, result.getStatusCodeValue());
		assertEquals(1, contents.length);
	}

	// GET CONTENT FOR ROLE_USER
	@Test
	public void getContent_AuthenticationInvalid_Failure() throws Exception {
		Content content = new Content("Spring Security Fundamentals", "summary", "content", "link1234567890", false);
		contents.save(content);
		Integer id = contents.save(content).getId();
		URI uri = new URI(baseUrl + port + "/api/contents/" + id);
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_USER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
		
		ResponseEntity<Content> result = restTemplate.withBasicAuth("admin", "goodpassword").getForEntity(uri, Content.class);
			
		assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void getContent_InvalidId_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/contents/1");
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
		
		ResponseEntity<Content> result = restTemplate.withBasicAuth("admin", "goodpassword").getForEntity(uri, Content.class);
			
		assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void getContent_ValidContentId_Success() throws Exception {
		Content content = new Content("Spring Security Fundamentals", "summary", "content", "link1234567890", true);
		Integer id = contents.save(content).getId();
		URI uri = new URI(baseUrl + port + "/api/contents/" + id);

		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
		
		ResponseEntity<Content> result = restTemplate.withBasicAuth("admin", "goodpassword").getForEntity(uri, Content.class);
			
		assertEquals(200, result.getStatusCode().value());
		assertEquals(content.getTitle(), result.getBody().getTitle());
	}

	@Test
	public void addContent_AuthenticationValid_Success() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/contents");
		Content content = new Content("Spring Security Fundamentals", "summary", "content", "link1234567890", true);
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));

		ResponseEntity<Content> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.postForEntity(uri, content, Content.class);
			
		assertEquals(201, result.getStatusCode().value());
		assertEquals(content.getTitle(), result.getBody().getTitle());
	}

	@Test
	public void addContent_AuthenticationInvalid_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/contents");
		Content content = new Content("Spring Security Fundamentals", "summary", "content", "link1234567890", true);
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_USER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));

		ResponseEntity<Content> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.postForEntity(uri, content, Content.class);
			
		assertEquals(403, result.getStatusCode().value());
	}

	@Test
    public void deleteContent_ValidContentId_Success() throws Exception {
		Content content = contents.save(new Content("Spring Security Fundamentals", "summary", "content", "link1234567890", true));
        URI uri = new URI(baseUrl + port + "/api/contents/" + content.getId());
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
        
        ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
                                      .exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(200, result.getStatusCode().value());
        // An empty Optional should be returned by "findById" after deletion
        Optional<Content> emptyValue = Optional.empty();
        assertEquals(emptyValue, contents.findById(content.getId()));
	}
	

    @Test
    public void deleteContent_InvalidContentId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/contents/1");
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
        
        ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
                                      .exchange(uri, HttpMethod.DELETE, null, Void.class);
      
        assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void deleteContent_InvalidAuthentication_Failure() throws Exception {
		Content content = contents.save(new Content("Spring Security Fundamentals", "summary", "content", "link1234567890", true));
		URI uri = new URI(baseUrl + port + "/api/contents/1");
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_USER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));

		ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
										.exchange(uri, HttpMethod.DELETE, null, Void.class);
		assertEquals(403, result.getStatusCode().value());
	}
	
	@Test
    public void updateContent_ValidContentId_Success() throws Exception {
		Content content = contents.save(new Content("Spring Security Fundamentals", "summary", "content", "link1234567890", false));
        URI uri = new URI(baseUrl + port + "/api/contents/" + content.getId());
        Content newContentInfo = new Content("Spring Security Fundamentals", "new_summary", "new_content", "new_link1234567890", true);
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
        
        ResponseEntity<Content> result = restTemplate.withBasicAuth("admin", "goodpassword")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newContentInfo), Content.class);
            
        assertEquals(200, result.getStatusCode().value());
        assertEquals(newContentInfo.getTitle(), result.getBody().getTitle());
    }

    @Test
    public void updateContent_InvalidContentId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/contents/1");
        Content newContentInfo = new Content("Spring Security Fundamentals", "new_summary", "new_content", "new_link1234567890", true);
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_MANAGER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
        
        ResponseEntity<Content> result = restTemplate.withBasicAuth("admin", "goodpassword")
 		.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newContentInfo), Content.class);
            
        assertEquals(404, result.getStatusCode().value());
	}
	
	@Test
    public void updateContent_InvalidAuthentication_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/contents/1");
        Content newContentInfo = new Content("Spring Security Fundamentals", "new_summary", "new_content", "new_link1234567890", false);
		users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_USER", "jimtan", "S9794462H", "81235768", "Jalan Cilandak 78 Mandala", true));
        
        ResponseEntity<Content> result = restTemplate.withBasicAuth("admin", "goodpassword")
 		.exchange(uri, HttpMethod.PUT, new HttpEntity<>(newContentInfo), Content.class);
            
        assertEquals(403, result.getStatusCode().value());
	}


}
