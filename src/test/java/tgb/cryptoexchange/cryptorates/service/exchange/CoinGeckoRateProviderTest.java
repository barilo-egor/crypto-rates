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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.CoinGeckoResponse;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;
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
class CoinGeckoRateProviderTest {

    @Mock
    private ExchangeWebClientFactory exchangeWebClientFactory;

    @InjectMocks
    private CoinGeckoRateProvider coinGeckoRateProvider;

    static List<CryptoPair> getCryptoPairs() {
        return Exchange.COIN_GECKO.getPairs();
    }

    @Test
    void shouldThrowExceptionIfPairNotConfigured() {
        Exchange fakeExchange = Mockito.mock(Exchange.class);
        Mockito.when(fakeExchange.getPairs()).thenReturn(List.of(CryptoPair.valueOf("ETH_USD")));
        assertThrows(CryptoRatesException.class, () -> new CoinGeckoRateProvider(exchangeWebClientFactory) {
            @Override
            public Exchange getExchange() {
                return fakeExchange;
            }
        });
    }

    @ParameterizedTest
    @ValueSource(strings = { "ETH_USD", "XMR_USD" })
    @DisplayName("getRate(CryptoPair cryptoPair) - валютная пара не обрабатываемая биржей CoinGecko - проброс UnsupportedCryptoPairException")
    void shouldThrowUnsupportedCryptoPairExceptionIfNotExchangeCryptoPair(CryptoPair cryptoPair) {
        assertThrows(UnsupportedCryptoPairException.class, () -> coinGeckoRateProvider.getRate(cryptoPair),
                "Unsupported crypto pair " + cryptoPair.name());
    }

    @ParameterizedTest
    @MethodSource("getCryptoPairs")
    @DisplayName("getRate(CryptoPair cryptoPair) - null ответ от exchangeWebClientFactory - возвращается null")
    void shouldReturnNullIfResponseIsNull(CryptoPair cryptoPair) {
        when(exchangeWebClientFactory.get(eq(Exchange.COIN_GECKO), any(), eq(CoinGeckoResponse.class))).thenReturn(
                null);
        BigDecimal actual = coinGeckoRateProvider.getRate(cryptoPair);
        assertNull(actual);
    }

    @ParameterizedTest
    @MethodSource("getCryptoPairs")
    @DisplayName("getRate(CryptoPair cryptoPair) - exchangeWebClientFactory возвращает ответ с курсом - возвращается курс")
    void shouldReturnRateForAllCryptoPairs(CryptoPair cryptoPair) {
        CoinGeckoResponse coinGeckoResponse = new CoinGeckoResponse();
        Map<String, Map<String, BigDecimal>> rates = new HashMap<>();
        Map<String, BigDecimal> bitcoinRates = new HashMap<>();
        BigDecimal usdBitcoin = new BigDecimal("0.01");
        bitcoinRates.put("usd", usdBitcoin);
        BigDecimal rubBitcoin = new BigDecimal("100");
        bitcoinRates.put("rub", rubBitcoin);
        rates.put("bitcoin", bitcoinRates);
        Map<String, BigDecimal> litecoinRates = new HashMap<>();
        BigDecimal usdLitecoin = new BigDecimal("1.50");
        litecoinRates.put("usd", usdLitecoin);
        BigDecimal rubLitecoin = new BigDecimal("24");
        litecoinRates.put("rub", rubLitecoin);
        rates.put("litecoin", litecoinRates);
        coinGeckoResponse.setRates(rates);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Function<UriBuilder, URI>> captor =
                (ArgumentCaptor<Function<UriBuilder, URI>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(Function.class);
        when(exchangeWebClientFactory.get(eq(Exchange.COIN_GECKO), captor.capture(),
                eq(CoinGeckoResponse.class))).thenReturn(coinGeckoResponse);

        BigDecimal actual = coinGeckoRateProvider.getRate(cryptoPair);
        switch (cryptoPair) {
        case BTC_USD -> assertEquals(usdBitcoin, actual);
        case LTC_USD -> assertEquals(usdLitecoin, actual);
        case BTC_RUB -> assertEquals(rubBitcoin, actual);
        case LTC_RUB -> assertEquals(rubLitecoin, actual);
        }

        Function<UriBuilder, URI> value = captor.getValue();
        URI result = value.apply(new DefaultUriBuilderFactory().builder());
        assertEquals("/simple/price", result.getPath());
        assertTrue(result.getQuery().startsWith("ids="));
        assertTrue(result.getQuery().contains("&vsCurrencies="));
    }

    @Test
    @DisplayName("getExchange() - простой вызов - возвращается биржа CoinGecko")
    void shouldReturnCoinGeckoExchange() {
        assertEquals(Exchange.COIN_GECKO, coinGeckoRateProvider.getExchange());
    }
}