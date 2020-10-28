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
    private PortfolioRepository portfolio;
    private AccountRepository accounts;
    private AccountService accountService;

    public AssetController(PortfolioRepository portfolio, AccountRepository accounts){
        this.portfolio = portfolio;
        this.accounts = accounts;
    }

    @GetMapping("/api/portfolio")
    public Portfolio getPortfolio() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (portfolio.findByCustomerId(user.getId()) == null) return makePortfolio(user.getId());
        return portfolio.findByCustomerId(user.getId());
    }
    
    public Portfolio makePortfolio(Integer id) {
        Portfolio temp = new Portfolio();
        temp.setCustomer_id(id);
        temp.setUnrealized_gain_loss(0);
        temp.setTotal_gain_loss(0);
        return portfolio.save(temp);
    }
}