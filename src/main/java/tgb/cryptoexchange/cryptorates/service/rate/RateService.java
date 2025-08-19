package tgb.cryptoexchange.cryptorates.service.rate;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.cryptorates.constants.CryptoPair;
import tgb.cryptoexchange.cryptorates.dto.CryptoRate;
import tgb.cryptoexchange.cryptorates.exception.CryptoRatesException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RateService {

    private final Map<CryptoPair, RateProvider> rateProviders;

    public RateService(List<RateProvider> rateProviders) {
        this.rateProviders = new HashMap<>();
        for (RateProvider rateProvider : rateProviders) {
            this.rateProviders.put(rateProvider.getCryptoPair(), rateProvider);
        }
    }

    public CryptoRate getRate(CryptoPair cryptoPair) {
        RateProvider rateProvider = rateProviders.get(cryptoPair);
        if (Objects.isNull(rateProvider)) {
            throw new CryptoRatesException("No provider found for " + cryptoPair);
        }
        return rateProvider.provide();
    }

}
