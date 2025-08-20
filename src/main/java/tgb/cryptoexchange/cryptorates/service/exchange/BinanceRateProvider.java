package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Component;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.BinanceResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class BinanceRateProvider extends ExchangeRateProvider {

    private final Map<CryptoPair, String> pairs;

    protected BinanceRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        super(exchangeWebClientFactory);
        this.pairs = new HashMap<>();
        this.pairs.put(CryptoPair.BTC_USD, "BTCUSDT");
        this.pairs.put(CryptoPair.LTC_USD, "LTCUSDT");
        this.pairs.put(CryptoPair.BTC_RUB, "BTCRUB");
        this.pairs.put(CryptoPair.LTC_RUB, "LTCRUB");
        this.pairs.put(CryptoPair.ETH_USD, "ETHUSDT");
        this.pairs.put(CryptoPair.TRX_USD, "TRXUSDT");
    }

    @Override
    public BigDecimal getRate(CryptoPair cryptoPair) {
        BinanceResponse response = exchangeWebClientFactory.get(
                getExchange(),
                uriBuilder -> uriBuilder.path("/avgPrice")
                        .queryParam("symbol", pairs.get(cryptoPair))
                        .build(),
                BinanceResponse.class
        );
        if (response == null) {
            return null;
        }
        return response.getPrice();
    }

    @Override
    public Exchange getExchange() {
        return Exchange.BINANCE;
    }

}
