package cs203.g1t7.asset;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import cs203.g1t7.asset.*;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import java.io.IOException;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;// import cs203.g1t7.users.*;

@RestController
public class QuoteController {
    private QuoteRepository quotes;

    public QuoteController(QuoteRepository quotes) {
        this.quotes = quotes;
    }

    /**
     * Search for content with the given id and given authorisation
     * If there is no content with the given "id", throw a ContentNotFoundException
     * @param id
     * @return content with the given id
     */
    @GetMapping("/api/stocks/{asset_id}")
    public Quote getQuote(@PathVariable String asset_id)
    {
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                .url(String.format("https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/v2/get-quotes?symbols=%s.SI&region=US", asset_id))
                .get()
                .addHeader("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "e1e10a7566msh7e27b7b32a32892p1b388cjsn656aa333f5cf")
                .build();
        
            Response response = client.newCall(request).execute();

            String jsonData = response.body().string();
            try {
                JSONObject Jobject = new JSONObject(jsonData);
                JSONObject quoteResponse = Jobject.getJSONObject("quoteResponse");
                JSONArray result = quoteResponse.getJSONArray("result");
                JSONObject stock = result.getJSONObject(0);
                
                Double price = stock.getDouble("regularMarketPrice");
                Double ask = stock.getDouble("ask");
                Double bid = stock.getDouble("bid");
                
                Integer askVolume = 20000;
                Integer bidVolume = 20000;

                Quote quote;
                if (quotes.findBySymbol(asset_id) != null) {
                    quote = quotes.findBySymbol(asset_id);
                    quote.setBid(bid);
                    quote.setAsk(ask);
                } else {
                    quote = new Quote(asset_id, price, bidVolume, bid, askVolume, ask);
                }
                return quotes.save(quote);
            } catch (JSONException e) {
                throw new QuoteNotFoundException(asset_id);
            }
        } catch (IOException e) {
            throw new QuoteNotFoundException(asset_id);
        }
    }
}