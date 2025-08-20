package tgb.cryptoexchange.cryptorates.service.rate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.dto.CryptoRate;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;
import tgb.cryptoexchange.cryptorates.service.exchange.ExchangeClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class RateService {

    private final Map<CryptoPair, List<ExchangeClient>> exchangeClients;

    public RateService(List<ExchangeClient> exchangeClients) {
        this.exchangeClients = new HashMap<>();
        for (ExchangeClient exchangeClient : exchangeClients) {
            for (CryptoPair cryptoPair : exchangeClient.getExchange().getPairs()) {
                this.exchangeClients.computeIfAbsent(cryptoPair, k -> new ArrayList<>()).add(exchangeClient);
            }
        }
    }

    public CryptoRate getRate(CryptoPair cryptoPair) {
        List<ExchangeClient> exchangeClients = this.exchangeClients.get(cryptoPair);
        if (exchangeClients == null || exchangeClients.isEmpty()) {
            throw new CryptoRatesException("No exchange clients found for " + cryptoPair);
        }
        for (ExchangeClient exchangeClient : exchangeClients) {
            try {
                BigDecimal rate = exchangeClient.getRate(cryptoPair);
                if (Objects.nonNull(rate)) {
                    CryptoRate cryptoRate = new CryptoRate();
                    cryptoRate.setRate(rate.setScale(8, RoundingMode.HALF_UP).doubleValue());
                    cryptoRate.setPair(cryptoPair);
                    cryptoRate.setTimestamp(System.currentTimeMillis());
                    return cryptoRate;
                }
            } catch (Exception e) {
                log.warn("Ошибка при получении курса валютной пары {} у биржи {}:",
                        cryptoPair.name(), exchangeClient.getExchange().name(), e);
            }
        }
        throw new CryptoRatesException("Couldn't get a rate from any exchange for " + cryptoPair.name());
    }

}
