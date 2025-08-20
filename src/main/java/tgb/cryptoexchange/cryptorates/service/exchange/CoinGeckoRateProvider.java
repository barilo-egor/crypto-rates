package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.CoinGeckoResponse;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;

@Service
public class CoinGeckoRateProvider implements ExchangeRateProvider {

    private final WebClient coinGeckoWebClient;

    public CoinGeckoRateProvider(WebClient coinGeckoWebClient) {
        this.coinGeckoWebClient = coinGeckoWebClient;
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
        CoinGeckoResponse response = coinGeckoWebClient.get()
                .uri(
                        uriBuilder -> uriBuilder.path("/simple/price")
                                .queryParam("ids", idsParam)
                                .queryParam("vsCurrencies", vsCurrenciesParam)
                                .build()
                )
                .retrieve()
                .bodyToMono(CoinGeckoResponse.class)
                .block();
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
