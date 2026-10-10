package com.evilcorp.onlinevoting.model;

import lombok.*;
import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VotingUser {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;

    public VotingUser(String username) {
        this.username = username;
    }
}
