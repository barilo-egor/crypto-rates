package tgb.cryptoexchange.cryptorates.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class CoinGeckoResponse {
    private Map<String, Map<String, BigDecimal>> rates = new HashMap<>();

    @JsonAnySetter
    public void setCurrencyRate(String baseCurrency, Map<String, BigDecimal> quoteCurrencies) {
        rates.put(baseCurrency, quoteCurrencies);
    }

    public BigDecimal getRate(String baseCurrency, String quoteCurrency) {
        return rates.get(baseCurrency).get(quoteCurrency);
    }
}
