package tgb.cryptoexchange.cryptorates.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static tgb.cryptoexchange.cryptorates.constants.CryptoPair.*;

/**
 * Константы бирж. Содержат список валют и базовый url.
 */
@AllArgsConstructor
@Getter
public enum Exchange {
    BINANCE(List.of(
            BTC_USD, LTC_USD, BTC_RUB, LTC_RUB, ETH_USD, TRX_USD
    ), "https://api1.binance.com/api/v3"),
    KRAKEN(List.of(
            XMR_USD
    ), "https://api.kraken.com/0/public"),
    COIN_GECKO(List.of(
            BTC_USD, LTC_USD, BTC_RUB, LTC_RUB
    ), "https://api.coingecko.com/api/v3"),
    EXCHANGE_RATE(List.of(
            USD_RUB
    ), "https://v6.exchangerate-api.com/v6")
    ;

    private final List<CryptoPair> pairs;

    private final String baseUrl;
}
