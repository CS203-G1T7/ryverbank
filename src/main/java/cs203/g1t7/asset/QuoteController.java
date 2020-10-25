package cs203.g1t7.asset;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import cs203.g1t7.asset.*;
// import cs203.g1t7.users.*;

@RestController
public class QuoteController {
    public QuoteController() {
    }

    /**
     * Search for content with the given id and given authorisation
     * If there is no content with the given "id", throw a ContentNotFoundException
     * @param id
     * @return content with the given id
     */
    @GetMapping("/accounts/quote/{asset_id}")
    public static Quote getQuote(@PathVariable String asset_id)
    {
        final String uri = String.format("https://finnhub.io/api/v1/stock/price-target?symbol=%s&token=bu3iu1v48v6up0bhtaeg", asset_id);
    
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        // {"lastUpdated":"","symbol":"","targetHigh":0,"targetLow":0,"targetMean":0,"targetMedian":0}
        String[] split = result.split("\"");
        
        String sym = split[7];
        Double price = Double.parseDouble(split[14].substring(1, split[14].length() - 2));

        Quote quote = new Quote(sym, price, 20000, price - 0.01, 20000, price + 0.01);

        return quote;
    }
}