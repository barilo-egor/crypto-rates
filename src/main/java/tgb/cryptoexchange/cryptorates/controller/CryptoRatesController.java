package tgb.cryptoexchange.cryptorates.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.dto.CryptoRate;
import tgb.cryptoexchange.web.ApiResponse;

@RestController
@RequestMapping("/crypto-rates")
public class CryptoRatesController {

    @GetMapping("/{pair}")
    public ApiResponse<CryptoRate> getCryptoRate(@PathVariable CryptoPair pair) {
        return null;
    }
}
