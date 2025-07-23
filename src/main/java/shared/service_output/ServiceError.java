package shared.service_output;

/**
 * Represents an error that occurred when executing a service.
 * Useful when a service needs to return multiple validation errors to be shown to the user simultaneously
 * which is not possible when throwing an exception.
 * @param message
 */
public record ServiceError(String message) {
    @Override public String toString() {
        return "ServiceError(%s)".formatted(message);
    }
}
