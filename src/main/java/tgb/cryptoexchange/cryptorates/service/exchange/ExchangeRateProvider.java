package tgb.cryptoexchange.cryptorates.service.exchange;

import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;

import java.math.BigDecimal;

/**
 * Провайдер для получения курса биржи.
 */
public abstract class ExchangeRateProvider {

    protected final ExchangeWebClientFactory exchangeWebClientFactory;

    protected ExchangeRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        this.exchangeWebClientFactory = exchangeWebClientFactory;
    }

    /**
     * Получение текущего курса для криптовалютной пары
     * @param cryptoPair криптовалютная пара
     * @return текущий курс пары
     */
    public abstract BigDecimal getRate(CryptoPair cryptoPair);

    /**
     * Получение биржи, используемой для получения курса
     * @return биржа
     */
    public abstract Exchange getExchange();
}
