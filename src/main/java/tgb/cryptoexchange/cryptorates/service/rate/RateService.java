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
import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RateService {

    private final Map<CryptoPair, List<ExchangeRateProvider>> exchangeClients;

    private final Map<CryptoPair, CryptoRateCache> cache = new EnumMap<>(CryptoPair.class);

    private final Integer ttlSeconds;

    private final Clock clock;

    public RateService(List<ExchangeRateProvider> exchangeRateProviders,
                       @Value("${rate.cache.ttl-seconds:15}") Integer ttlSeconds,
                       Clock clock) {
        this.ttlSeconds = ttlSeconds;
        this.clock = clock;
        this.exchangeClients = new EnumMap<>(CryptoPair.class);
        for (ExchangeRateProvider exchangeRateProvider : exchangeRateProviders) {
            for (CryptoPair cryptoPair : exchangeRateProvider.getExchange().getPairs()) {
                log.trace("Для валютной пары {} добавлен провайдер биржи {}", cryptoPair.name(), exchangeRateProvider.getExchange().name());
                this.exchangeClients.computeIfAbsent(cryptoPair, k -> new ArrayList<>()).add(exchangeRateProvider);
            }
        }
        if (log.isDebugEnabled()) {
            for (Map.Entry<CryptoPair, List<ExchangeRateProvider>> entry : this.exchangeClients.entrySet()) {
                log.debug("Для валютной пары {} добавлены следующие биржи: {}",
                        entry.getKey().name(),
                        entry.getValue().stream()
                                .map(prov -> prov.getExchange().name())
                                .collect(Collectors.joining(","))
                );
            }
        }
    }

    public CryptoRate getRate(CryptoPair cryptoPair) {
        CryptoRateCache cryptoRateCache = this.cache.get(cryptoPair);
        if (Objects.nonNull(cryptoRateCache) && isValid(cryptoRateCache)) {
            return cryptoRateCache.cryptoRate();
        }
        List<ExchangeRateProvider> exchangeRateProviders = this.exchangeClients.get(cryptoPair);
        if (exchangeRateProviders == null) {
            throw new ProviderNotFoundException("No exchange clients found for " + cryptoPair);
        }
        for (ExchangeRateProvider exchangeRateProvider : exchangeRateProviders) {
            try {
                BigDecimal rate = exchangeRateProvider.getRate(cryptoPair);
                if (Objects.nonNull(rate)) {
                    return createCryptoRate(cryptoPair, rate);
                }
            } catch (Exception e) {
                log.error("Ошибка при получении курса валютной пары {} у биржи {}:",
                        cryptoPair.name(), exchangeRateProvider.getExchange().name(), e);
            }
        }
        throw new CryptoRatesException("Couldn't get a rate from any exchange for " + cryptoPair.name());
    }

    private boolean isValid(CryptoRateCache cache) {
        return cache.timestamp() + (ttlSeconds * 1000) > clock.millis();
    }

    private CryptoRate createCryptoRate(CryptoPair cryptoPair, BigDecimal rate) {
        CryptoRate cryptoRate = new CryptoRate();
        cryptoRate.setRate(rate.setScale(8, RoundingMode.HALF_UP).doubleValue());
        cryptoRate.setPair(cryptoPair);
        cryptoRate.setTimestamp(clock.millis());
        this.cache.put(cryptoPair, new CryptoRateCache(cryptoRate, clock.millis()));
        return cryptoRate;
    }
}
