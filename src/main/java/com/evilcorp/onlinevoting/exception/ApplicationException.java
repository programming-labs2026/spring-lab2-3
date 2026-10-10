package com.evilcorp.onlinevoting.exception;

import lombok.Getter;
import java.util.Objects;

public abstract class ApplicationException extends RuntimeException {
    private static final String LOG_MESSAGE_REQUIRED = "Log message must not be null";
    private static final String MESSAGE_KEY_REQUIRED = "Message key must not be null";
    private static final String ARGUMENTS_REQUIRED = "Message arguments must not be null";

    @Getter
    private final String messageKey;
    private final Object[] args;

    protected ApplicationException(String messageKey, String logMessage, Object... args) {
        super(Objects.requireNonNull(logMessage, LOG_MESSAGE_REQUIRED));
        this.messageKey = Objects.requireNonNull(messageKey, MESSAGE_KEY_REQUIRED);
        this.args = Objects.requireNonNull(args, ARGUMENTS_REQUIRED).clone();
    }

    protected ApplicationException(String messageKey, String logMessage, Throwable cause, Object... args) {
        super(Objects.requireNonNull(logMessage, LOG_MESSAGE_REQUIRED), cause);
        this.messageKey = Objects.requireNonNull(messageKey, MESSAGE_KEY_REQUIRED);
        this.args = Objects.requireNonNull(args, ARGUMENTS_REQUIRED).clone();
    }

    public Object[] getArgs() {
        return args.clone();
    }
}
