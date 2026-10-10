package com.evilcorp.onlinevoting.repository;

import java.util.UUID;
import java.util.Optional;
import com.evilcorp.onlinevoting.model.VotingUser;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface VotingUserRepository extends JpaRepository<VotingUser, UUID> {
    Optional<VotingUser> findByUsername(String username);
}
