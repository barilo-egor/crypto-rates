package tgb.cryptoexchange.cryptorates.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CryptoRatesConfiguration {

    @Bean
    public WebClient binanceWebClient() {
        return WebClient.create("https://api1.binance.com/api/v3");
    }

    @Bean
    public WebClient krakenWebClient() {
        return WebClient.create("https://api.kraken.com/0/public");
    }

    @Bean
    public WebClient coinGeckoWebClient() {
        return WebClient.create("https://api.coingecko.com/api/v3");
    }

    @Bean
    public WebClient exchangeRateWebClient() {
        return WebClient.create("https://v6.exchangerate-api.com/v6");
    }
}
