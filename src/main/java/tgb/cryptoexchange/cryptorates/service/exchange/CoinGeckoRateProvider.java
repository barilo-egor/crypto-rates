package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.CoinGeckoResponse;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;

@Service
public class CoinGeckoRateProvider extends ExchangeRateProvider {

    protected CoinGeckoRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        super(exchangeWebClientFactory);
    }

    @Override
    public BigDecimal getRate(CryptoPair cryptoPair) {
        String idsParam = switch (cryptoPair) {
            case BTC_USD, BTC_RUB -> "bitcoin";
            case LTC_USD, LTC_RUB -> "litecoin";
            default -> throw new UnsupportedCryptoPairException("Unsupported crypto pair");
        };
        String vsCurrenciesParam = switch (cryptoPair) {
            case BTC_USD, LTC_USD -> "usd";
            case BTC_RUB, LTC_RUB -> "rub";
            default -> throw new UnsupportedCryptoPairException("Unsupported crypto pair");
        };
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
