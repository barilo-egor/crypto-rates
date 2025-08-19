package tgb.cryptoexchange.cryptorates.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static tgb.cryptoexchange.cryptorates.constants.CryptoPair.*;

@AllArgsConstructor
@Getter
public enum Exchange {
    BINANCE(List.of(
            BTC_USD, LTC_USD, BTC_RUB, LTC_RUB, ETH_USD, TRX_USD
    )),
    KRAKEN(List.of(
            XMR_USD
    )),
    COIN_GECKO(List.of(
            BTC_USD, LTC_USD, BTC_RUB, LTC_RUB
    )),
    EXCHANGE_RATE(List.of(
            USD_RUB
    ))
    ;

    private final List<CryptoPair> pairs;
}
