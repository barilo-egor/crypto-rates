package tgb.cryptoexchange.cryptorates.service.exchange;

import org.springframework.stereotype.Component;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;
import tgb.cryptoexchange.cryptorates.dto.BinanceResponse;
import tgb.cryptoexchange.cryptorates.exception.UnsupportedCryptoPairException;

import java.math.BigDecimal;

@Component
public class BinanceRateProvider extends ExchangeRateProvider {

    protected BinanceRateProvider(ExchangeWebClientFactory exchangeWebClientFactory) {
        super(exchangeWebClientFactory);
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
            default -> throw new UnsupportedCryptoPairException("Unsupported crypto pair");
        };
        BinanceResponse response = exchangeWebClientFactory.get(
                getExchange(),
                uriBuilder -> uriBuilder.path("/avgPrice")
                        .queryParam("symbol", pair)
                        .build(),
                BinanceResponse.class
        );
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
