package com.evilcorp.onlinevoting.dto.request;

import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CastVoteRequest {
    @Setter(AccessLevel.NONE)
    private UUID votingId;

    @NotNull(message = "{validation.user.required}")
    private UUID userId;

    @NotNull(message = "{validation.vote.candidate.required}")
    private UUID candidateId;

    public CastVoteRequest forVoting(@NonNull UUID votingId) {
        this.votingId = votingId;
        return this;
    }
}
