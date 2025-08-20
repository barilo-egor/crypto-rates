package tgb.cryptoexchange.cryptorates.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.dto.CryptoRate;
import tgb.cryptoexchange.cryptorates.service.rate.RateService;
import tgb.cryptoexchange.web.ApiResponse;

@RestController
@RequestMapping("/crypto-rates")
public class CryptoRatesController {

    private final RateService rateService;

    public CryptoRatesController(RateService rateService) {
        this.rateService = rateService;
    }

    @GetMapping("/{pair}")
    public ResponseEntity<ApiResponse<CryptoRate>> getCryptoRate(@PathVariable CryptoPair pair) {
        return new ResponseEntity<>(
                ApiResponse.success(rateService.getRate(pair)),
                HttpStatus.OK
        );
    }
}
