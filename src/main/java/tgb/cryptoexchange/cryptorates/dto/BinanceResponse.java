package tgb.cryptoexchange.cryptorates.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Ответ от API биржи {@link tgb.cryptoexchange.cryptorates.constants.Exchange#BINANCE}
 */
@Data
public class BinanceResponse {

    private BigDecimal price;
}
