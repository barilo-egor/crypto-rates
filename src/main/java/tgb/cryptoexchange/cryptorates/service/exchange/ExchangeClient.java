package tgb.cryptoexchange.cryptorates.service.exchange;

import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.constants.Exchange;

import java.math.BigDecimal;

public interface ExchangeClient {

    BigDecimal getRate(CryptoPair cryptoPair);

    Exchange getExchange();
}
