package tgb.cryptoexchange.cryptorates.service.rate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.CryptoRate;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;
import tgb.cryptoexchange.cryptorates.exception.ProviderNotFoundException;
import tgb.cryptoexchange.cryptorates.service.exchange.BinanceRateProvider;
import tgb.cryptoexchange.cryptorates.service.exchange.CoinGeckoRateProvider;
import tgb.cryptoexchange.cryptorates.service.exchange.KrakenRateProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static tgb.cryptoexchange.cryptorates.constants.CryptoPair.USD_RUB;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {

    @Mock
    private BinanceRateProvider binanceRateProvider;

    @Mock
    private CoinGeckoRateProvider coinGeckoRateProvider;

    @Mock
    private KrakenRateProvider krakenRateProvider;

    @Mock
    private Clock clock;

    private RateService rateService;

    @BeforeEach
    void setUp() {
        when(binanceRateProvider.getExchange()).thenReturn(Exchange.BINANCE);
        when(coinGeckoRateProvider.getExchange()).thenReturn(Exchange.COIN_GECKO);
        when(krakenRateProvider.getExchange()).thenReturn(Exchange.KRAKEN);
        this.rateService = new RateService(Arrays.asList(binanceRateProvider, coinGeckoRateProvider, krakenRateProvider), 15, clock);
    }

    @Test
    void shouldThrowExceptionWhenRateProviderNotFound() {
        assertThrows(ProviderNotFoundException.class, () -> rateService.getRate(USD_RUB), "No exchange clients found for USD_RUB");
    }

    @ValueSource(strings = {"BTC_USD", "LTC_USD"})
    @ParameterizedTest
    void shouldReturnRate(CryptoPair cryptoPair) {
        BigDecimal rate = new BigDecimal("1234.4432123153526346");
        Long millis = 1786453423453L;
        when(binanceRateProvider.getRate(cryptoPair)).thenReturn(rate);
        when(clock.millis()).thenReturn(millis);
        CryptoRate actual = rateService.getRate(cryptoPair);
        assertAll(
                () -> assertEquals(rate.setScale(8, RoundingMode.HALF_UP).doubleValue(), actual.getRate()),
                () -> assertEquals(cryptoPair, actual.getPair()),
                () -> assertEquals(millis, actual.getTimestamp())
        );
    }

    @ValueSource(strings = {"BTC_USD", "LTC_USD"})
    @ParameterizedTest
    void shouldReturnRateFromNextExchangeIfFirstThrowsException(CryptoPair cryptoPair) {
        BigDecimal rate = new BigDecimal("1234.4432123153526346");
        Long millis = 1786453423453L;
        when(binanceRateProvider.getRate(cryptoPair)).thenThrow(NullPointerException.class);
        when(coinGeckoRateProvider.getRate(cryptoPair)).thenReturn(rate);
        when(clock.millis()).thenReturn(millis);
        CryptoRate actual = rateService.getRate(cryptoPair);
        assertAll(
                () -> assertEquals(rate.setScale(8, RoundingMode.HALF_UP).doubleValue(), actual.getRate()),
                () -> assertEquals(cryptoPair, actual.getPair()),
                () -> assertEquals(millis, actual.getTimestamp())
        );
    }

    @ValueSource(strings = {"BTC_USD", "LTC_USD"})
    @ParameterizedTest
    void shouldThrowCryptoRatesExceptionIfAllExchangesThrowsException(CryptoPair cryptoPair) {
        when(binanceRateProvider.getRate(cryptoPair)).thenThrow(NullPointerException.class);
        when(coinGeckoRateProvider.getRate(cryptoPair)).thenThrow(IllegalArgumentException.class);
        assertThrows(CryptoRatesException.class, () -> rateService.getRate(cryptoPair),
                "Couldn't get a rate from any exchange for " + cryptoPair.name());
    }

    @ValueSource(strings = {"BTC_USD", "LTC_USD"})
    @ParameterizedTest
    void shouldReturnCachedRate(CryptoPair cryptoPair) {
        BigDecimal rate = new BigDecimal("1234.4432123153526346");
        Long millis = 1786453423453L;
        when(binanceRateProvider.getRate(cryptoPair)).thenReturn(rate);
        when(clock.millis()).thenReturn(millis);
        rateService.getRate(cryptoPair);
        CryptoRate actual = rateService.getRate(cryptoPair);
        verify(binanceRateProvider, times(1)).getRate(cryptoPair);
        assertAll(
                () -> assertEquals(rate.setScale(8, RoundingMode.HALF_UP).doubleValue(), actual.getRate()),
                () -> assertEquals(cryptoPair, actual.getPair()),
                () -> assertEquals(millis, actual.getTimestamp())
        );
    }

    @ValueSource(strings = {"BTC_USD", "LTC_USD"})
    @ParameterizedTest
    void shouldReturnRateFromNextExchangeIfFirstReturnsNull(CryptoPair cryptoPair) {
        BigDecimal rate = new BigDecimal("1234.4432123153526346");
        Long millis = 1786453423453L;
        when(binanceRateProvider.getRate(cryptoPair)).thenReturn(null);
        when(coinGeckoRateProvider.getRate(cryptoPair)).thenReturn(rate);
        when(clock.millis()).thenReturn(millis);
        CryptoRate actual = rateService.getRate(cryptoPair);
        assertAll(
                () -> assertEquals(rate.setScale(8, RoundingMode.HALF_UP).doubleValue(), actual.getRate()),
                () -> assertEquals(cryptoPair, actual.getPair()),
                () -> assertEquals(millis, actual.getTimestamp())
        );
    }

    @ValueSource(strings = {"BTC_USD", "LTC_USD"})
    @ParameterizedTest
    void shouldReturnNewRateIfCacheNotValid(CryptoPair cryptoPair) {
        BigDecimal rate = new BigDecimal("1234.4432123153526346");
        Long millis = 1786453423453L;
        when(binanceRateProvider.getRate(cryptoPair)).thenReturn(rate);
        when(clock.millis()).thenReturn(millis);

        rateService.getRate(cryptoPair);

        Long expiredMillis = 1886453423453L;
        when(clock.millis()).thenReturn(expiredMillis);
        BigDecimal newRate = new BigDecimal("900.12412412");
        when(binanceRateProvider.getRate(cryptoPair)).thenReturn(newRate);

        CryptoRate actual = rateService.getRate(cryptoPair);

        verify(binanceRateProvider, times(2)).getRate(cryptoPair);
        assertAll(
                () -> assertEquals(newRate.setScale(8, RoundingMode.HALF_UP).doubleValue(), actual.getRate()),
                () -> assertEquals(cryptoPair, actual.getPair()),
                () -> assertEquals(expiredMillis, actual.getTimestamp())
        );
    }
}