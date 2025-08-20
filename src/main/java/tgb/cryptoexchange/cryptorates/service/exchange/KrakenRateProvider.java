package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Component;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.KrakenResponse;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;

@Component
public class KrakenRateProvider extends ExchangeRateProvider {

    protected KrakenRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        super(exchangeWebClientFactory);
    }

    @Override
    public BigDecimal getRate(CryptoPair cryptoPair) {
        if (!getExchange().getPairs().contains(cryptoPair)) {
            throw new UnsupportedCryptoPairException("Unsupported crypto pair: " + cryptoPair);
        }
        String pair = "XMRUSD";
        KrakenResponse response = exchangeWebClientFactory.get(
                getExchange(),
                uriBuilder -> uriBuilder.path("/Ticker")
                        .queryParam("pair", pair)
                        .build(),
                KrakenResponse.class
        );
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
