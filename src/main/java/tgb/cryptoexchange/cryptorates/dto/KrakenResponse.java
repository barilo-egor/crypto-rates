package tgb.cryptoexchange.cryptorates.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class KrakenResponse {

    private Result result;

    @Data
    @NoArgsConstructor
    public static class Result {
        private Map<String, CurrencyPairData> currencyPairs = new HashMap<>();

        @JsonAnySetter
        public void setCurrencyPair(String pairName, CurrencyPairData data) {
            this.currencyPairs.put(pairName, data);
        }
    }

    @Data
    @NoArgsConstructor
    public static class CurrencyPairData {

        @JsonProperty("a")
        private List<BigDecimal> ask;
    }
}
