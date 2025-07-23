package shared.service_output;

import java.util.List;

/**
 * Abstract base class representing data and operation common to all service output.
 */
public abstract class ServiceOutput {
    /* can be exceptions that occurred or validation errors */
    private final List<ServiceError> errors;

    /**
     * @param errors The errors that occurred when performing the service (exceptions or validation errors).
     */
    public ServiceOutput(List<ServiceError> errors) {
        this.errors = errors;
    }

    /**
     * @return The list of errors occurred when performing the service (exceptions or validation errors).
     */
    public List<ServiceError> errors() {
        return errors;
    }

    /**
     * @return True if no errors occurred, else false.
     */
    public boolean ok() {
        return errors.isEmpty();
    }
}
