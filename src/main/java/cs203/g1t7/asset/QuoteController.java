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
import cs203.g1t7.trade.*;

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
    private TradeRepository trade;

    public QuoteController(QuoteRepository quotes, TradeRepository trade) {
        this.quotes = quotes;
        this.trade = trade;
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
                .addHeader("x-rapidapi-key", "9a41233dd9msh98664491aaf1edep1390efjsncf7844a7c528")
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
                    List<Trade> allAssetTradeFilteredBySymbol = trade.findBySymbol(asset_id);
                    if (allAssetTradeFilteredBySymbol != null && allAssetTradeFilteredBySymbol.size() != 0) {
                        double bestAsk = Double.MAX_VALUE;
                        int bestAskVolume = 0;
                        for (int i = allAssetTradeFilteredBySymbol.size() - 1; i >= 0; i--) {            
                            Trade latestTrade = allAssetTradeFilteredBySymbol.get(allAssetTradeFilteredBySymbol.size() - 1);
                    
                            String status = latestTrade.getStatus();
                            if ((!status.equals("partial-filled") || !status.equals("open")) && (latestTrade.getAsk() == 0 || latestTrade.getAction().equals("buy"))) continue;
                    
                            double latestAsk = latestTrade.getAsk();
                    
                            if (latestAsk < bestAsk) {
                                bestAsk = latestAsk;
                                bestAskVolume = latestTrade.getQuantity() - latestTrade.getFilled_quantity();
                            }
                            // break;
                        }
                    
                        double bestBid = 0.0;
                        int bestBidVolume = 0;
                        for (int i = allAssetTradeFilteredBySymbol.size() - 1; i >= 0; i--) {           
                            Trade latestTrade = allAssetTradeFilteredBySymbol.get(allAssetTradeFilteredBySymbol.size() - 1);
                    
                            String status = latestTrade.getStatus();
                            if ((!status.equals("partial-filled") || !status.equals("open")) && (latestTrade.getBid() == 0 || latestTrade.getAction().equals("sell"))) continue;
                    
                            double latestBid = latestTrade.getBid();
                    
                            if (latestBid > bestBid) {
                                bestBid = latestBid;
                                bestBidVolume = latestTrade.getQuantity() - latestTrade.getFilled_quantity();
                            } 
                            // break;
                        }
                        if (bestAsk != Double.MAX_VALUE && quote.getOriginalAskVol() <= 0) {
                            quote.setAsk(bestAsk);
                            quote.setAsk_volume(bestAskVolume);
                        }
                        if (bestBid != 0.0 && quote.getOriginalBidVol() <= 0) {
                            quote.setBid(bestBid);
                            quote.setBid_volume(bestBidVolume);
                        }
                    }
                } else {
                    quote = new Quote(asset_id, price, bidVolume, bid, askVolume, ask, askVolume, bidVolume);
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