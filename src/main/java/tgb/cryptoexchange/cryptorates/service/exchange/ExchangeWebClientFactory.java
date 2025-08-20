package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import tgb.cryptoexchange.cryptorates.constants.Exchange;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class ExchangeWebClientFactory {

    private final Map<Exchange, WebClient> webClients;

    public ExchangeWebClientFactory() {
        this.webClients = new HashMap<>();
        for (Exchange exchange : Exchange.values()) {
            webClients.put(exchange, WebClient.builder().baseUrl(exchange.getBaseUrl()).build());
        }
    }

    public <T> T get(Exchange exchange, Function<UriBuilder, URI> uriBuilder, Class<T> responseType) {
        return webClients.get(exchange)
                .get()
                .uri(uriBuilder)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}
