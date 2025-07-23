package profile.view;

/**
 * Simple test implementation of ISignUpView for unit testing
 */
public class TestSignUpView implements ISignUpView {
    private Runnable submitCallback;
    private RawInput formInput;
    private String lastError;
    private String lastSuccess;
    private boolean closed = false;

    public TestSignUpView() {
        this.formInput = new RawInput(
            "John Doe",
            "1999-07-01",
            "175.0",
            "70.0",
            "MALE",
            "METRIC"
        );
    }

    @Override
    public void setOnSubmit(Runnable callback) {
        this.submitCallback = callback;
    }

    @Override
    public RawInput getFormInput() {
        return formInput;
    }

    @Override
    public void showError(String msg) {
        this.lastError = msg;
    }

    @Override
    public void showSuccess(String msg) {
        this.lastSuccess = msg;
    }

    @Override
    public void close() {
        this.closed = true;
    }

    public void setFormInput(RawInput input) {
        this.formInput = input;
    }

    public void triggerSubmit() {
        if (submitCallback != null) {
            submitCallback.run();
        }
    }

    public String getLastError() {
        return lastError;
    }

    public String getLastSuccess() {
        return lastSuccess;
    }

    public boolean isClosed() {
        return closed;
    }

    public void reset() {
        this.lastError = null;
        this.lastSuccess = null;
        this.closed = false;
    }
}
