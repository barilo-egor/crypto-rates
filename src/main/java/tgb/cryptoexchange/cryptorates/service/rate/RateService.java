package tgb.cryptoexchange.cryptorates.service.rate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptorates.cache.CryptoRateCache;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.dto.CryptoRate;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;
import tgb.cryptoexchange.cryptorates.exception.ProviderNotFoundException;
import tgb.cryptoexchange.cryptorates.service.exchange.ExchangeRateProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class RateService {

    private final Map<CryptoPair, List<ExchangeRateProvider>> exchangeClients;

    private final Map<CryptoPair, CryptoRateCache> cache = new HashMap<>();

    private final Integer ttlSeconds;

    public RateService(List<ExchangeRateProvider> exchangeRateProviders, @Value("${rate.cache.ttl-seconds:15}") Integer ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
        this.exchangeClients = new EnumMap<>(CryptoPair.class);
        for (ExchangeRateProvider exchangeRateProvider : exchangeRateProviders) {
            for (CryptoPair cryptoPair : exchangeRateProvider.getExchange().getPairs()) {
                this.exchangeClients.computeIfAbsent(cryptoPair, k -> new ArrayList<>()).add(exchangeRateProvider);
            }
        }
    }

    public CryptoRate getRate(CryptoPair cryptoPair) {
        CryptoRateCache cryptoRateCache = this.cache.get(cryptoPair);
        if (isValid(cryptoRateCache)) {
            return cryptoRateCache.cryptoRate();
        }
        List<ExchangeRateProvider> exchangeRateProviders = this.exchangeClients.get(cryptoPair);
        if (exchangeRateProviders == null || exchangeRateProviders.isEmpty()) {
            throw new ProviderNotFoundException("No exchange clients found for " + cryptoPair);
        }
        for (ExchangeRateProvider exchangeRateProvider : exchangeRateProviders) {
            try {
                if (exchangeRateProvider.getExchange().getPairs().contains(cryptoPair)) {
                    BigDecimal rate = exchangeRateProvider.getRate(cryptoPair);
                    if (Objects.nonNull(rate)) {
                        return createCryptoRate(cryptoPair, rate);
                    }
                }
            } catch (Exception e) {
                log.warn("Ошибка при получении курса валютной пары {} у биржи {}:",
                        cryptoPair.name(), exchangeRateProvider.getExchange().name(), e);
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
