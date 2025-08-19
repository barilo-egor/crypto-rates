package tgb.cryptoexchange.cryptorates.service.rate;

import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.dto.CryptoRate;
import tgb.cryptoexchange.cryptorates.service.exchange.ExchangeClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public abstract class RateProvider {

    protected final List<ExchangeClient> exchangeClients;

    public RateProvider(List<ExchangeClient> exchangeClients) {
        this.exchangeClients = new ArrayList<>();
        for (ExchangeClient exchangeClient : exchangeClients) {
            if (exchangeClient.getExchange().getPairs().contains(getCryptoPair())) {
                this.exchangeClients.add(exchangeClient);
            }
        }
    }

    public CryptoRate provide() {
        BigDecimal rate = getRate();
        CryptoRate cryptoRate = new CryptoRate();
        cryptoRate.setPair(getCryptoPair());
        cryptoRate.setRate(rate.setScale(8, RoundingMode.HALF_UP).doubleValue());
        cryptoRate.setTimestamp(System.currentTimeMillis());
        return cryptoRate;
    }

    protected abstract BigDecimal getRate();

    public abstract CryptoPair getCryptoPair();
}
