package com.evilcorp.onlinevoting.dto.request;

import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class OwnerActionRequest {
    @Setter(AccessLevel.NONE)
    private UUID votingId;

    @NotNull(message = "{validation.owner.required}")
    private UUID userId;

    public OwnerActionRequest forVoting(@NonNull UUID votingId) {
        this.votingId = votingId;
        return this;
    }
}
