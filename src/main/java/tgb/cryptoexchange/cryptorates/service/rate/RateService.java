package tgb.cryptoexchange.cryptorates.service.rate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptorates.cache.CryptoRateCache;
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

    private final Map<CryptoPair, CryptoRateCache> cache = new HashMap<>();

    private final Integer ttlSeconds;

    public RateService(List<ExchangeClient> exchangeClients, @Value("${rate.cache.ttl-seconds:15}") Integer ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
        this.exchangeClients = new HashMap<>();
        for (ExchangeClient exchangeClient : exchangeClients) {
            for (CryptoPair cryptoPair : exchangeClient.getExchange().getPairs()) {
                this.exchangeClients.computeIfAbsent(cryptoPair, k -> new ArrayList<>()).add(exchangeClient);
            }
        }
    }

    public CryptoRate getRate(CryptoPair cryptoPair) {
        CryptoRateCache cache = this.cache.get(cryptoPair);
        if (isValid(cache)) {
            return cache.cryptoRate();
        }
        List<ExchangeClient> exchangeClients = this.exchangeClients.get(cryptoPair);
        if (exchangeClients == null || exchangeClients.isEmpty()) {
            throw new CryptoRatesException("No exchange clients found for " + cryptoPair);
        }
        for (ExchangeClient exchangeClient : exchangeClients) {
            try {
                BigDecimal rate = exchangeClient.getRate(cryptoPair);
                if (Objects.nonNull(rate)) {
                    return createCryptoRate(cryptoPair, rate);
                }
            } catch (Exception e) {
                log.warn("Ошибка при получении курса валютной пары {} у биржи {}:",
                        cryptoPair.name(), exchangeClient.getExchange().name(), e);
            }
        }
        throw new CryptoRatesException("Couldn't get a rate from any exchange for " + cryptoPair.name());
    }

    private boolean isValid(CryptoRateCache cache) {
        return cache.timestamp() + (ttlSeconds * 1000) > System.currentTimeMillis();
    }

    private CryptoRate createCryptoRate(CryptoPair cryptoPair, BigDecimal rate) {
        CryptoRate cryptoRate = new CryptoRate();
        cryptoRate.setRate(rate.setScale(8, RoundingMode.HALF_UP).doubleValue());
        cryptoRate.setPair(cryptoPair);
        cryptoRate.setTimestamp(System.currentTimeMillis());
        this.cache.put(cryptoPair, new CryptoRateCache(cryptoRate, System.currentTimeMillis()));
        return cryptoRate;
    }
}
