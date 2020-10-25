package cs203.g1t7.asset;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import cs203.g1t7.account.*;
import cs203.g1t7.users.*;

@RestController
public class AssetController {
    private AssetRepository portfolio;
    private AccountRepository accounts;
    private AccountService accountService;

    public AssetController(AssetRepository portfolio, AccountRepository accounts){
        this.portfolio = portfolio;
        this.accounts = accounts;
    }

    @GetMapping("/api/portfolio")
    public List<Asset> getPortfolio() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return portfolio.findByCustomerId(user.getId());
    }
}