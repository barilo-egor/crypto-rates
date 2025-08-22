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
import tgb.cryptoexchange.cryptorates.dto.KrakenResponse;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KrakenRateProviderTest {

    @Mock
    private ExchangeWebClientFactory exchangeWebClientFactory;

    @InjectMocks
    private KrakenRateProvider krakenRateProvider;

    static List<CryptoPair> getCryptoPairs() {
        return Exchange.KRAKEN.getPairs();
    }

    @ParameterizedTest
    @ValueSource(strings = { "ETH_USD", "BTC_USD" })
    @DisplayName("getRate(CryptoPair cryptoPair) - валютная пара не обрабатываемая биржей Kraken - проброс UnsupportedCryptoPairException")
    void shouldThrowUnsupportedCryptoPairExceptionIfNotExchangeCryptoPair(CryptoPair cryptoPair) {
        assertThrows(UnsupportedCryptoPairException.class, () -> krakenRateProvider.getRate(cryptoPair),
                "Unsupported crypto pair " + cryptoPair.name());
    }

    @ParameterizedTest
    @MethodSource("getCryptoPairs")
    @DisplayName("getRate(CryptoPair cryptoPair) - null ответ от exchangeWebClientFactory - возвращается null")
    void shouldReturnNullIfResponseIsNull(CryptoPair cryptoPair) {
        when(exchangeWebClientFactory.get(eq(Exchange.KRAKEN), any(), eq(KrakenResponse.class))).thenReturn(
                null);
        BigDecimal actual = krakenRateProvider.getRate(cryptoPair);
        assertNull(actual);
    }

    @ParameterizedTest
    @MethodSource("getCryptoPairs")
    @DisplayName("getRate(CryptoPair cryptoPair) - exchangeWebClientFactory возвращает ответ с курсом - возвращается курс")
    void shouldReturnRateForAllCryptoPairs(CryptoPair cryptoPair) {
        KrakenResponse krakenResponse = new KrakenResponse();
        BigDecimal rate = new BigDecimal("124.636");
        KrakenResponse.Result responseResult = new KrakenResponse.Result();
        KrakenResponse.CurrencyPairData currencyPairData = new KrakenResponse.CurrencyPairData();
        currencyPairData.setAsk(List.of(rate));
        responseResult.setCurrencyPair("XXMRZUSD", currencyPairData);
        krakenResponse.setResult(responseResult);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Function<UriBuilder, URI>> captor =
                (ArgumentCaptor<Function<UriBuilder, URI>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(Function.class);
        when(exchangeWebClientFactory.get(eq(Exchange.KRAKEN), captor.capture(),
                eq(KrakenResponse.class))).thenReturn(krakenResponse);

        BigDecimal actual = krakenRateProvider.getRate(cryptoPair);
        assertEquals(rate, actual);

        Function<UriBuilder, URI> value = captor.getValue();
        URI result = value.apply(new DefaultUriBuilderFactory().builder());
        assertEquals("/Ticker", result.getPath());
        assertTrue(result.getQuery().contains("pair=XMRUSD"));
    }

    @Test
    @DisplayName("getExchange() - простой вызов - возвращается биржа Kraken")
    void shouldReturnCoinGeckoExchange() {
        assertEquals(Exchange.KRAKEN, krakenRateProvider.getExchange());
    }
}