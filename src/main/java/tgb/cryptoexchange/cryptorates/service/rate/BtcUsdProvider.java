package tgb.cryptoexchange.cryptorates.service.rate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;
import tgb.cryptoexchange.cryptorates.service.exchange.ExchangeClient;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class BtcUsdProvider extends RateProvider {

    public BtcUsdProvider(List<ExchangeClient> exchangeClients) {
        super(exchangeClients);
    }

    @Override
    protected BigDecimal getRate() {
        for (ExchangeClient exchangeClient : exchangeClients) {
            try {
                BigDecimal rate = exchangeClient.getRate(getCryptoPair());
                if (rate != null) {
                    return rate;
                }
            } catch (Exception e) {
                log.warn(
                        "Ошибка при попытке получения курса пары {} биржи {}:",
                        getCryptoPair().name(),
                        exchangeClient.getExchange().name(),
                        e
                );
            }
        }
        throw new CryptoRatesException("No rates found for " + getCryptoPair());
    }

    @Override
    public CryptoPair getCryptoPair() {
        return CryptoPair.BTC_USD;
    }
}
