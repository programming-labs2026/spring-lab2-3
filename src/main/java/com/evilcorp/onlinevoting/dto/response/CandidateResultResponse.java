package com.evilcorp.onlinevoting.dto.response;

import java.util.UUID;

public record CandidateResultResponse(
        UUID id,
        String name,
        long votes
) {}
