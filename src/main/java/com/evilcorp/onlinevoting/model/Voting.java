package com.evilcorp.onlinevoting.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import jakarta.persistence.*;
import com.evilcorp.onlinevoting.model.enums.VotingStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Voting {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private VotingUser owner;

    @Enumerated(EnumType.STRING)
    private VotingStatus status = VotingStatus.DRAFT;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidate> candidates = new ArrayList<>();

    public Voting(String title, VotingUser owner) {
        this.title = title;
        this.owner = owner;
    }

    public Candidate addCandidate(String name) {
        Candidate candidate = new Candidate(this, name);
        candidates.add(candidate);
        return candidate;
    }

    public void update(String title, List<String> candidateNames) {
        this.title = title;
        candidates.clear();
        candidateNames.forEach(this::addCandidate);
    }

    public void changeStatus(VotingStatus status) {
        this.status = status;
    }
}
