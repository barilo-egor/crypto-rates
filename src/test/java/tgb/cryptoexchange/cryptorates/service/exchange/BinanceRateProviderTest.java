package tgb.cryptoexchange.cryptorates.service.exchange;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.BinanceResponse;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BinanceRateProviderTest {

    @Mock
    private ExchangeWebClientFactory exchangeWebClientFactory;

    @InjectMocks
    private BinanceRateProvider binanceRateProvider;

    static List<CryptoPair> getCryptoPairs() {
        return Exchange.BINANCE.getPairs();
    }

    @ParameterizedTest
    @MethodSource("getCryptoPairs")
    void shouldReturnNullIfResponseIsNull(CryptoPair cryptoPair) {
        when(exchangeWebClientFactory.get(eq(Exchange.BINANCE), any(), eq(BinanceResponse.class))).thenReturn(null);
        BigDecimal actual = binanceRateProvider.getRate(cryptoPair);
        assertNull(actual);
    }

    @ParameterizedTest
    @MethodSource("getCryptoPairs")
    void shouldReturnRateForAllCryptoPairs(CryptoPair cryptoPair) {
        BinanceResponse binanceResponse = new BinanceResponse();
        binanceResponse.setPrice(BigDecimal.ONE);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Function<UriBuilder, URI>> captor =
                (ArgumentCaptor<Function<UriBuilder, URI>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(Function.class);
        when(exchangeWebClientFactory.get(eq(Exchange.BINANCE), captor.capture(), eq(BinanceResponse.class))).thenReturn(binanceResponse);
        BigDecimal actual = binanceRateProvider.getRate(cryptoPair);
        Function<UriBuilder, URI> value = captor.getValue();
        URI result = value.apply(new DefaultUriBuilderFactory().builder());
        assertEquals("/avgPrice", result.getPath());
        assertTrue(result.getQuery().startsWith("symbol="));
        assertEquals(binanceResponse.getPrice(), actual);
    }
}