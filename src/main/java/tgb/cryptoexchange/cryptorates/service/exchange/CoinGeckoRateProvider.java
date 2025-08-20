package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.CoinGeckoResponse;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CoinGeckoRateProvider extends ExchangeRateProvider {

    private final Map<CryptoPair, String> idsParams;

    private final Map<CryptoPair, String> vsCurrenciesParams;

    protected CoinGeckoRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        super(exchangeWebClientFactory);
        this.idsParams = new HashMap<>();
        this.idsParams.put(CryptoPair.BTC_USD, "bitcoin");
        this.idsParams.put(CryptoPair.LTC_USD, "litecoin");
        this.idsParams.put(CryptoPair.BTC_RUB, "bitcoin");
        this.idsParams.put(CryptoPair.LTC_RUB, "litecoin");
        this.vsCurrenciesParams = new HashMap<>();
        this.vsCurrenciesParams.put(CryptoPair.BTC_USD, "usd");
        this.vsCurrenciesParams.put(CryptoPair.LTC_USD, "usd");
        this.vsCurrenciesParams.put(CryptoPair.BTC_RUB, "rub");
        this.vsCurrenciesParams.put(CryptoPair.LTC_RUB, "rub");
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
            return null;
        }
        return response.getRate(idsParam, vsCurrenciesParam);
    }

    @Override
    public Exchange getExchange() {
        return Exchange.COIN_GECKO;
    }

}
