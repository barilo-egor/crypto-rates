package tgb.cryptoexchange.cryptorates.service.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.BinanceResponse;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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
        if (!getExchange().getPairs().contains(cryptoPair)) {
            throw new UnsupportedCryptoPairException("Unsupported crypto pair: " + cryptoPair);
        }
        BinanceResponse response = exchangeWebClientFactory.get(
                getExchange(),
                uriBuilder -> uriBuilder.path("/avgPrice")
                        .queryParam("symbol", pairs.get(cryptoPair))
                        .build(),
                BinanceResponse.class
        );
        if (response == null) {
            log.warn("Ответ от Binance равен null для валютной пары {}.", cryptoPair.name());
            return null;
        }
        return response.getPrice();
    }

    @Override
    public Exchange getExchange() {
        return Exchange.BINANCE;
    }

}
