package tgb.cryptoexchange.cryptorates.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@ComponentScan(basePackages = {"tgb.cryptoexchange"})
public class CryptoRatesConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
