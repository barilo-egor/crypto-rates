package tgb.cryptoexchange.cryptorates.service.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.CoinGeckoResponse;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Реализация получения курсов биржи {@link Exchange#COIN_GECKO}
 */
@Service
@Slf4j
public class CoinGeckoRateProvider extends ExchangeRateProvider {

    private final Map<CryptoPair, String> idsParams = Map.of(
            CryptoPair.BTC_USD, "bitcoin",
            CryptoPair.LTC_USD, "litecoin",
            CryptoPair.BTC_RUB, "bitcoin",
            CryptoPair.LTC_RUB, "litecoin"
    );

    private final Map<CryptoPair, String> vsCurrenciesParams = Map.of(
            CryptoPair.BTC_USD, "usd",
            CryptoPair.LTC_USD, "usd",
            CryptoPair.BTC_RUB, "rub",
            CryptoPair.LTC_RUB, "rub"
    );

    protected CoinGeckoRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        super(exchangeWebClientFactory);
        if (!getExchange().getPairs().stream().allMatch(idsParams::containsKey)
                || !getExchange().getPairs().stream().allMatch(vsCurrenciesParams::containsKey)) {
            throw new CryptoRatesException("Some CoinGecko crypto pair does not have params.");
        }
    }

    @Override
    public BigDecimal getRate(CryptoPair cryptoPair) {
        if (!getExchange().getPairs().contains(cryptoPair)) {
            throw new UnsupportedCryptoPairException("Unsupported crypto pair: " + cryptoPair);
        }
        String idsParam = idsParams.get(cryptoPair);
        String vsCurrenciesParam = vsCurrenciesParams.get(cryptoPair);
        CoinGeckoResponse response = exchangeWebClientFactory.get(
                Exchange.COIN_GECKO,
                uriBuilder -> uriBuilder.path("/simple/price")
                        .queryParam("ids", idsParam)
                        .queryParam("vsCurrencies", vsCurrenciesParam)
                        .build(),
                CoinGeckoResponse.class
        );
        if (response == null) {
            log.warn("Ответ от CoinGecko равен null для валютной пары {}.", cryptoPair.name());
            return null;
        }
        return response.getRate(idsParam, vsCurrenciesParam);
    }

    @Override
    public Exchange getExchange() {
        return Exchange.COIN_GECKO;
    }

}
