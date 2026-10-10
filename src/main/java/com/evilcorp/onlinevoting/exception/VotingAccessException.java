package com.evilcorp.onlinevoting.exception;

public final class VotingAccessException extends ApplicationException {
    private static final String MESSAGE_KEY = "error.voting.not-owner";

    public VotingAccessException(String logMessage) {
        super(MESSAGE_KEY, logMessage);
    }
}
