package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import tgb.cryptoexchange.cryptorates.constants.Exchange;

import java.net.URI;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Фабрика для выполнения запросов к биржам.
 */
@Component
public class ExchangeWebClientFactory {

    private final Map<Exchange, WebClient> webClients;

    public ExchangeWebClientFactory() {
        this.webClients = new EnumMap<>(Exchange.class);
        for (Exchange exchange : Exchange.values()) {
            webClients.put(exchange, WebClient.builder().baseUrl(exchange.getBaseUrl()).build());
        }
    }

    /**
     * Выполнение GET запроса через {@link WebClient} переданной в параметре биржи.
     * @param exchange биржа, к которой требуется выполнить запрос
     * @param uriBuilder функция для формирования uri
     * @param responseType класс типа ответа
     * @return ответ биржи на запрос
     * @param <T> тип ответа
     */
    public <T> T get(Exchange exchange, Function<UriBuilder, URI> uriBuilder, Class<T> responseType) {
        return webClients.get(exchange)
                .get()
                .uri(uriBuilder)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}
