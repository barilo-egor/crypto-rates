package tgb.cryptoexchange.cryptorates.service.exchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.ExchangeRateResponse;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateRateProviderTest {

    @Mock
    private ExchangeWebClientFactory exchangeWebClientFactory;

    @InjectMocks
    private ExchangeRateRateProvider exchangeRateRateProvider;

    static List<CryptoPair> getCryptoPairs() {
        return Exchange.EXCHANGE_RATE.getPairs();
    }

    @ParameterizedTest
    @ValueSource(strings = { "ETH_USD", "XMR_USD" })
    @DisplayName("getRate(CryptoPair cryptoPair) - валютная пара не обрабатываемая биржей ExchangeRate - проброс UnsupportedCryptoPairException")
    void shouldThrowUnsupportedCryptoPairExceptionIfNotExchangeCryptoPair(CryptoPair cryptoPair) {
        assertThrows(UnsupportedCryptoPairException.class, () -> exchangeRateRateProvider.getRate(cryptoPair),
                "Unsupported crypto pair " + cryptoPair.name());
    }

    @ParameterizedTest
    @MethodSource("getCryptoPairs")
    @DisplayName("getRate(CryptoPair cryptoPair) - null ответ от exchangeWebClientFactory - возвращается null")
    void shouldReturnNullIfResponseIsNull(CryptoPair cryptoPair) {
        when(exchangeWebClientFactory.get(eq(Exchange.EXCHANGE_RATE), any(), eq(ExchangeRateResponse.class))).thenReturn(
                null);
        BigDecimal actual = exchangeRateRateProvider.getRate(cryptoPair);
        assertNull(actual);
    }

    @ParameterizedTest
    @MethodSource("getCryptoPairs")
    @DisplayName("getRate(CryptoPair cryptoPair) - exchangeWebClientFactory возвращает ответ с курсом - возвращается курс")
    void shouldReturnRateForAllCryptoPairs(CryptoPair cryptoPair) {
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();
        Map<String, BigDecimal> rates = new HashMap<>();
        BigDecimal rate = new BigDecimal("124.636");
        rates.put("RUB", rate);
        exchangeRateResponse.setConversionRates(rates);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Function<UriBuilder, URI>> captor =
                (ArgumentCaptor<Function<UriBuilder, URI>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(Function.class);
        when(exchangeWebClientFactory.get(eq(Exchange.EXCHANGE_RATE), captor.capture(),
                eq(ExchangeRateResponse.class))).thenReturn(exchangeRateResponse);

        BigDecimal actual = exchangeRateRateProvider.getRate(cryptoPair);
        assertEquals(rate, actual);

        Function<UriBuilder, URI> value = captor.getValue();
        URI result = value.apply(new DefaultUriBuilderFactory().builder());
        assertEquals("/8ae628548cbe656cdc6f0a9e/latest/USD", result.getPath());
    }

    @Test
    @DisplayName("getExchange() - простой вызов - возвращается биржа EXCHANGE_RATE")
    void shouldReturnExchangeRateExchange() {
        assertEquals(Exchange.EXCHANGE_RATE, exchangeRateRateProvider.getExchange());
    }
}