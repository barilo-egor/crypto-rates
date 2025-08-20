package tgb.cryptoexchange.cryptorates.service.exchange;

import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;

import java.math.BigDecimal;

public abstract class ExchangeRateProvider {

    protected final ExchangeWebClientFactory exchangeWebClientFactory;

    protected ExchangeRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        this.exchangeWebClientFactory = exchangeWebClientFactory;
    }

    public abstract BigDecimal getRate(CryptoPair cryptoPair);

    public abstract Exchange getExchange();
}
