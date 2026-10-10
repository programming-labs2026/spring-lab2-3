package com.evilcorp.onlinevoting.dto.response;

import java.util.UUID;
import com.evilcorp.onlinevoting.model.enums.VotingStatus;

public record VotingSummaryResponse(
        UUID id,
        String title,
        String ownerName,
        VotingStatus status
) {}
