package com.evilcorp.onlinevoting.model;

import lombok.*;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voting_id")
    private Voting voting;

    private String name;

    public Candidate(Voting voting, String name) {
        this.voting = voting;
        this.name = name;
    }
}
