package tgb.cryptoexchange.cryptorates.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;

@Data
@Schema(description = "Представление курса криптовалюты")
public class CryptoRate {

    @Schema(description = "Валютная пара")
    private CryptoPair pair;

    @Schema(description = "Значение курса")
    private Double rate;

    @Schema(description = "Штамп времени в мс когда был получен курс")
    private Long timestamp;
}
