package com.evilcorp.onlinevoting.repository;

import java.util.List;
import java.util.UUID;
import com.evilcorp.onlinevoting.model.Vote;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    @Query("""
            select vote.candidate.id as candidateId, count(vote) as votes
            from Vote vote
            where vote.voting.id = :votingId
            group by vote.candidate.id
            """)
    List<CandidateVoteCount> countVotesByVotingId(@Param("votingId") UUID votingId);

    interface CandidateVoteCount {
        UUID getCandidateId();
        long getVotes();
    }

    boolean existsByVotingIdAndUserId(UUID votingId, UUID userId);
}
