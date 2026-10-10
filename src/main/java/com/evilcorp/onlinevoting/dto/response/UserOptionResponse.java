package com.evilcorp.onlinevoting.dto.response;

import java.util.UUID;

public record UserOptionResponse(
        UUID id,
        String username
) {}
