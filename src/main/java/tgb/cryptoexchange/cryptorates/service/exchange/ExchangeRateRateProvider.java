package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Component;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.ExchangeRateResponse;

import java.math.BigDecimal;

@Component
public class ExchangeRateRateProvider extends ExchangeRateProvider {

    protected ExchangeRateRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        super(exchangeWebClientFactory);
    }

    @Override
    public BigDecimal getRate(CryptoPair cryptoPair) {
        ExchangeRateResponse response = exchangeWebClientFactory.get(getExchange(),
                uriBuilder -> uriBuilder.path("/8ae628548cbe656cdc6f0a9e/latest/USD").build(),
                ExchangeRateResponse.class
        );
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
