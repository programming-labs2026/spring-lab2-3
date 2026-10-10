package com.evilcorp.onlinevoting.dto.request;

import lombok.*;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class CreateVotingRequest {
    @NotNull(message = "{validation.owner.required}")
    private UUID ownerId;

    @NotBlank(message = "{validation.title.required}")
    @Size(max = 255, message = "{validation.title.size}")
    private String title;

    @NotNull(message = "{validation.candidates.required}")
    @Size(min = 2, message = "{validation.candidates.min-two}")
    private List<
            @NotBlank(message = "{validation.candidate.required}")
            @Size(max = 255, message = "{validation.candidate.size}")
                    String
            > candidateNames = new ArrayList<>(List.of("", ""));
}
