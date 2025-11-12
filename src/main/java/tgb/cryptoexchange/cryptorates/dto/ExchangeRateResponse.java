package tgb.cryptoexchange.cryptorates.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Ответ от API биржи {@link tgb.cryptoexchange.cryptorates.constants.Exchange#EXCHANGE_RATE}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateResponse {

    @JsonProperty("conversion_rates")
    private Map<String, BigDecimal> conversionRates;

    public BigDecimal getRate(String currencyCode) {
        return conversionRates != null ? conversionRates.get(currencyCode) : null;
    }
}
