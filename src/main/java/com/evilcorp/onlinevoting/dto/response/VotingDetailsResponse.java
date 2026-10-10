package com.evilcorp.onlinevoting.dto.response;

import java.util.List;
import java.util.UUID;
import com.evilcorp.onlinevoting.model.enums.VotingStatus;

public record VotingDetailsResponse(
        UUID id,
        String title,
        UUID ownerId,
        String ownerName,
        VotingStatus status,
        List<CandidateOptionResponse> candidates
) {
    public VotingDetailsResponse {
        candidates = List.copyOf(candidates);
    }
}
