package com.evilcorp.onlinevoting.dto.response;

import java.util.List;
import java.util.UUID;
import com.evilcorp.onlinevoting.model.enums.VotingStatus;

public record VotingResultsResponse(
        UUID votingId,
        String title,
        VotingStatus status,
        List<CandidateResultResponse> candidates,
        long totalVotes
) {
    public VotingResultsResponse {
        candidates = List.copyOf(candidates);
    }
}
