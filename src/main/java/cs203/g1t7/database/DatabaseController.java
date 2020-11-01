package cs203.g1t7.database;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cs203.g1t7.client.RestTemplateClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import cs203.g1t7.account.AccountRepository;
import cs203.g1t7.asset.AssetRepository;
import cs203.g1t7.asset.PortfolioRepository;
import cs203.g1t7.asset.QuoteRepository;
import cs203.g1t7.content.ContentRepository;
import cs203.g1t7.trade.TradeRepository;
import cs203.g1t7.transaction.TransactionRepository;
import cs203.g1t7.users.UserRepository;

// import cs203.g1t7.asset.*;
import cs203.g1t7.users.*;
// importo cs203

@RestController
public class DatabaseController {
    private UserRepository userRepository;
    private ContentRepository contentRepository;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AssetRepository assetRepository;
    private PortfolioRepository portfolioRepository;
    private TradeRepository tradeRepository;
    private QuoteRepository quoteRepository;
    private BCryptPasswordEncoder encoder;

    public DatabaseController(UserRepository userRepository, ContentRepository contentRepository,
    AccountRepository accountRepository,  TransactionRepository transactionRepository, AssetRepository assetRepository,
    TradeRepository tradeRepository, PortfolioRepository portfolioRepository, QuoteRepository quoteRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.assetRepository = assetRepository;
        this.portfolioRepository = portfolioRepository;
        this.tradeRepository = tradeRepository;
        this.quoteRepository = quoteRepository;
        this.encoder = encoder;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/api/reset")
    public void reset()
    {
        contentRepository.deleteAll();
        userRepository.deleteAll();
        accountRepository.deleteAll();
        assetRepository.deleteAll();
        portfolioRepository.deleteAll();
        tradeRepository.deleteAll();
        quoteRepository.deleteAll();
        transactionRepository.deleteAll();

        // userRepository.save(new User("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER", "Raymond Tan", "S5118309F", "91251234", "27 Jalan Hidup S680234", true));
        // userRepository.save(new User("analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST", "Abang John", "S7251849G", "82345678", "27 Bukit Timah S123456", true));
        // userRepository.save(new User("analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST", "Alice Wong", "S5558664J", "92345678", "127 Bukit Batok", true));

        System.out.println("[Add user]: " + userRepository.save(
            new User("manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER", "Raymond Tan", "S5118309F", "91251234", "27 Jalan Hidup S680234", true)));
        System.out.println("[Add user]: " + userRepository.save(
            new User("analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST", "Abang John", "S7251849G", "82345678", "27 Bukit Timah S123456", true)));
        System.out.println("[Add user]: " + userRepository.save(
            new User("analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST", "Alice Wong", "S5558664J", "92345678", "127 Bukit Batok", true)));
    }
}