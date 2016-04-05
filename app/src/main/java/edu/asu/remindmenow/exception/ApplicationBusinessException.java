package edu.asu.remindmenow.exception;

import edu.asu.remindmenow.models.Message;

/**
 * Created by priyama on 4/5/2016.
 */


@SuppressWarnings("serial")
public class ApplicationBusinessException extends Exception {

    private Message errorMessage;

    public ApplicationBusinessException() {}

    public ApplicationBusinessException(Message message) {
        this.errorMessage = message;
    }

    public Message getErrorMessage() {
        return errorMessage;
    }

    public ApplicationBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationBusinessException(Throwable cause) {
        super(cause);
    }

}
