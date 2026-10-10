package com.evilcorp.onlinevoting.exception;

public final class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String messageKey, String logMessage) {
        super(messageKey, logMessage);
    }
}
