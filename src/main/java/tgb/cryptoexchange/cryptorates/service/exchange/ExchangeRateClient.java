package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.ExchangeRateResponse;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;

import java.math.BigDecimal;

@Component
public class ExchangeRateClient implements ExchangeClient{

    private final WebClient exchangeRateWebClient;

    public ExchangeRateClient(WebClient exchangeRateWebClient) {
        this.exchangeRateWebClient = exchangeRateWebClient;
    }

    @Override
    public BigDecimal getRate(CryptoPair cryptoPair) {
        if (CryptoPair.USD_RUB.equals(cryptoPair)) {
            throw new CryptoRatesException("Unsupported crypto pair");
        }
        ExchangeRateResponse response = exchangeRateWebClient.get()
                .uri("/8ae628548cbe656cdc6f0a9e/latest/USD")
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();
        if (response == null) {
            return null;
        }
        return response.getRate("RUB");
    }

    @Override
    public Exchange getExchange() {
        return Exchange.EXCHANGE_RATE;
    }

}
