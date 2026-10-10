package com.evilcorp.onlinevoting.dto.response;

import java.util.UUID;

public record CandidateOptionResponse(
        UUID id,
        String name
) {}
