package cs203.g1t7.trade;

import java.util.List;
import java.util.Optional;

import cs203.g1t7.transaction.*;
import cs203.g1t7.account.*;
import cs203.g1t7.users.*;
import cs203.g1t7.asset.*;

public interface TradeService {

    public void processTrade(Trade newTrade, boolean update);

    public void updateTrade();

    public Trade _updateTrade(Trade temp);

    public Portfolio updatePortfolio(Portfolio user, Optional<List<Asset>> temp);

    public Quote updateQuote(Quote temp, String condition, Integer volume, Double price);

    public Quote updateQuotePrice(Quote temp, Double price);

    public Transaction updateBalanceSender(Integer id_from, Transaction t);

    public Transaction updateBalanceReceiver(Integer id_to, Transaction t);
}