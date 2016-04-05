package edu.asu.remindmenow.exception;

import edu.asu.remindmenow.models.Message;

/**
 * Created by priyama on 4/5/2016.
 */
@SuppressWarnings("serial")
public class ApplicationRuntimeException extends RuntimeException {
    private Message errorMessage;

    public Message getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Message errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ApplicationRuntimeException(String message) {
        super(message);
    }

    public ApplicationRuntimeException(Message message) {
        this.setErrorMessage(message);
    }

    public ApplicationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationRuntimeException(Throwable cause) {
        super(cause);
    }
}
