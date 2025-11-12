package tgb.cryptoexchange.cryptorates.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.dto.CryptoRate;
import tgb.cryptoexchange.cryptorates.service.rate.RateService;
import tgb.cryptoexchange.web.ApiResponse;
import tgb.cryptoexchange.web.LogResponseBody;

@Slf4j
@RestController
@RequestMapping("/crypto-rates")
@LogResponseBody
public class CryptoRatesController extends ApiController {

    private final RateService rateService;

    public CryptoRatesController(RateService rateService) {
        this.rateService = rateService;
    }

    @Operation(
            summary = "Получение курса криптовалютной пары.",
            description = "Получение текущего курса любой из доступных бирж по валютной паре."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Курс получен."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера.",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{pair}")
    public ResponseEntity<ApiResponse<CryptoRate>> getCryptoRate(@PathVariable CryptoPair pair) {
        log.trace("Запрос на получение курса для пары {}", pair.name());
        return new ResponseEntity<>(
                ApiResponse.success(rateService.getRate(pair)),
                HttpStatus.OK
        );
    }
}
