package tgb.cryptoexchange.cryptorates.service.exchange;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class KrakenClientTest {

    @Autowired
    private KrakenClient krakenClient;

    @Test
    void shouldReturnRate() {
        BigDecimal rate = krakenClient.getRate(CryptoPair.XMR_USD);
        System.out.println("COURSE: " + rate);
        assertNotNull(rate);
    }
}