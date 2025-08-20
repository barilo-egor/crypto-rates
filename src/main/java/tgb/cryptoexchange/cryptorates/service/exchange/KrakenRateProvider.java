package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.KrakenResponse;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;

@Component
public class KrakenRateProvider implements ExchangeRateProvider {

    private final WebClient krakenWebClient;

    public KrakenRateProvider(WebClient krakenWebClient) {
        this.krakenWebClient = krakenWebClient;
    }

    @Override
    public BigDecimal getRate(CryptoPair cryptoPair) {
        if (!CryptoPair.XMR_USD.equals(cryptoPair)) {
            throw new UnsupportedCryptoPairException("Unsupported crypto pair");
        }
        String pair = "XMRUSD";
        KrakenResponse response = krakenWebClient.get()
                .uri(
                        uriBuilder -> uriBuilder.path("/Ticker")
                                .queryParam("pair", pair)
                                .build()
                )
                .retrieve()
                .bodyToMono(KrakenResponse.class)
                .block();
        if (response == null) {
            return null;
        }
        String responsePair = "XXMRZUSD";
        return response.getResult().getCurrencyPairs().get(responsePair).getAsk().getFirst();
    }

    @Override
    public Exchange getExchange() {
        return Exchange.KRAKEN;
    }

}
