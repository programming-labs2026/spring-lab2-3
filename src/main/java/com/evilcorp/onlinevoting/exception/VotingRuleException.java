package com.evilcorp.onlinevoting.exception;

public final class VotingRuleException extends ApplicationException {
    public VotingRuleException(String messageKey, String logMessage) {
        super(messageKey, logMessage);
    }

    public VotingRuleException(String messageKey, String logMessage, Throwable cause) {
        super(messageKey, logMessage, cause);
    }
}
