package com.evilcorp.onlinevoting.dto.request;

import lombok.*;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class UpdateVotingRequest {
    @Setter(AccessLevel.NONE)
    private UUID votingId;

    @NotNull(message = "{validation.owner.required}")
    private UUID userId;

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

    @Builder
    private UpdateVotingRequest(UUID userId, String title, List<String> candidateNames) {
        this.userId = userId;
        this.title = title;
        this.candidateNames = new ArrayList<>(candidateNames);
    }

    public UpdateVotingRequest forVoting(@NonNull UUID votingId) {
        this.votingId = votingId;
        return this;
    }
}
