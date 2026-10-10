package com.evilcorp.onlinevoting.config;

import lombok.NonNull;
import java.util.UUID;

public record VotingLinks(String baseUrl) {
    public VotingLinks(@NonNull String baseUrl) {
        String trimmed = baseUrl.strip();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException("Public base URL must not be blank");
        }
        this.baseUrl = trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    public String votingUrl(@NonNull UUID votingId) {
        return baseUrl + "/votings/" + votingId + "/vote";
    }

    public String resultsUrl(UUID votingId) {
        return baseUrl + "/votings/" + votingId + "/results";
    }
}
