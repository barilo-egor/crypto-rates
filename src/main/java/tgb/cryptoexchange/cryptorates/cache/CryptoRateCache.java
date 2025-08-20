package tgb.cryptoexchange.cryptorates.cache;

import tgb.cryptoexchange.cryptorates.dto.CryptoRate;

public record CryptoRateCache (CryptoRate cryptoRate, long timestamp) {
}
