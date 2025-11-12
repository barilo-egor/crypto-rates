package tgb.cryptoexchange.cryptorates.exception;

/**
 * Базовое исключение микросервиса crypto-rates
 */
public class CryptoRatesException extends RuntimeException {

    public CryptoRatesException(String message) {
        super(message);
    }

}
