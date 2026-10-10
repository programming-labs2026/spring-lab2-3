package com.evilcorp.onlinevoting.model;

import lombok.*;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voting_id")
    private Voting voting;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private VotingUser user;

    public Vote(Voting voting, Candidate candidate, VotingUser user) {
        this.voting = voting;
        this.candidate = candidate;
        this.user = user;
    }
}
