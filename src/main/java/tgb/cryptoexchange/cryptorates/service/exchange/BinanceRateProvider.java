package tgb.cryptoexchange.cryptorates.service.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.BinanceResponse;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class BinanceRateProvider extends ExchangeRateProvider {

    private final Map<CryptoPair, String> pairs = Map.of(
            CryptoPair.BTC_USD, "BTCUSDT",
            CryptoPair.LTC_USD, "LTCUSDT",
            CryptoPair.BTC_RUB, "BTCRUB",
            CryptoPair.LTC_RUB, "LTCRUB",
            CryptoPair.ETH_USD, "ETHUSDT",
            CryptoPair.TRX_USD, "TRXUSDT"
    );

    protected BinanceRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        super(exchangeWebClientFactory);
        if (!getExchange().getPairs().stream().allMatch(pairs::containsKey)) {
            throw new CryptoRatesException("Some Binance crypto pair does not have param.");
        }
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
