package tgb.cryptoexchange.cryptorates.exception;

/**
 * Исключение показывает, что для данной валютной пары отсутствует реализация получения курса.
 */
public class ProviderNotFoundException extends RuntimeException {
    public ProviderNotFoundException(String message) {
        super(message);
    }
}
