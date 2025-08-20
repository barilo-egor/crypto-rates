package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.CoinGeckoResponse;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class CoinGeckoRateProvider extends ExchangeRateProvider {

    protected CoinGeckoRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        super(exchangeWebClientFactory);
    }

    @Override
    public BigDecimal getRate(CryptoPair cryptoPair) {
        String idsParam = null;
        if (CryptoPair.BTC_USD.equals(cryptoPair) || CryptoPair.BTC_RUB.equals(cryptoPair)) {
            idsParam = "bitcoin";
        } else if (CryptoPair.LTC_USD.equals(cryptoPair) || CryptoPair.LTC_RUB.equals(cryptoPair)) {
            idsParam = "litecoin";
        }
        String vsCurrenciesParam = null;
        if (CryptoPair.BTC_USD.equals(cryptoPair) || CryptoPair.LTC_USD.equals(cryptoPair)) {
            vsCurrenciesParam = "usd";
        } else if (CryptoPair.BTC_RUB.equals(cryptoPair) || CryptoPair.LTC_RUB.equals(cryptoPair)) {
            vsCurrenciesParam = "rub";
        }
        if (Objects.isNull(idsParam)) {
            throw new CryptoRatesException("Null \"idsParam\" parameter");
        }
        final String finalIdsParam = idsParam;
        final String finalVsCurrenciesParam = vsCurrenciesParam;
        CoinGeckoResponse response = exchangeWebClientFactory.get(
                Exchange.COIN_GECKO,
                uriBuilder -> uriBuilder.path("/simple/price")
                        .queryParam("ids", finalIdsParam)
                        .queryParam("vsCurrencies", finalVsCurrenciesParam)
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
