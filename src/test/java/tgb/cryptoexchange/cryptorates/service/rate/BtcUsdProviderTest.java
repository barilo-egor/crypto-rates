package tgb.cryptoexchange.cryptorates.service.rate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@SpringBootTest
class BtcUsdProviderTest {
    @Autowired
    private BtcUsdProvider btcUsdProvider;

    @Test
    void shouldReturnRate() {
        BigDecimal actual = btcUsdProvider.getRate();
        System.out.println("COURSE = " + actual);
        assertNotNull(actual);
    }
}