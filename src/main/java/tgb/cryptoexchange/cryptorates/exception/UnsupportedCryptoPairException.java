package tgb.cryptoexchange.cryptorates.exception;

/**
 * Исключение показывает, что биржа не поддерживает криптовалютную пару.
 */
public class UnsupportedCryptoPairException extends CryptoRatesException {

    public UnsupportedCryptoPairException(String message) {
        super(message);
    }

}
