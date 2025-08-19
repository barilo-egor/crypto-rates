package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.BinanceResponse;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;

import java.math.BigDecimal;

@Component
public class BinanceClient implements ExchangeClient {

    private final WebClient binanceWebClient;

    public BinanceClient(WebClient binanceWebClient) {
        this.binanceWebClient = binanceWebClient;
    }

    @Override
    public BigDecimal getRate(CryptoPair cryptoPair) {
        String pair = switch (cryptoPair) {
            case BTC_USD -> "BTCUSDT";
            case LTC_USD -> "LTCUSDT";
            case BTC_RUB -> "BTCRUB";
            case LTC_RUB -> "LTCRUB";
            case ETH_USD -> "ETHUSDT";
            case TRX_USD -> "TRXUSDT";
            default -> throw new CryptoRatesException("Unsupported crypto pair");
        };
        BinanceResponse response = binanceWebClient.get()
                .uri(
                        uriBuilder -> uriBuilder.path("/avgPrice")
                                .queryParam("symbol", pair)
                                .build()
                )
                .retrieve()
                .bodyToMono(BinanceResponse.class)
                .block();
        if (response == null) {
            return null;
        }
        return response.getPrice();
    }

    @Override
    public Exchange getExchange() {
        return Exchange.BINANCE;
    }

}
