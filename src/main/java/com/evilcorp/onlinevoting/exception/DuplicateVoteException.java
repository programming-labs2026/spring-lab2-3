package com.evilcorp.onlinevoting.exception;

public final class DuplicateVoteException extends ApplicationException {
    private static final String MESSAGE_KEY = "error.attempt.duplicate";

    public DuplicateVoteException(String logMessage) {
        super(MESSAGE_KEY, logMessage);
    }
}
