package com.evilcorp.onlinevoting.repository;

import java.util.UUID;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import com.evilcorp.onlinevoting.model.Voting;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface VotingRepository extends JpaRepository<Voting, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Voting v where v.id = :id")
    Optional<Voting> findByIdForUpdate(@Param("id") UUID id);
}
