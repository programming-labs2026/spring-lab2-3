package com.evilcorp.onlinevoting.service;

import com.evilcorp.onlinevoting.model.Candidate;
import com.evilcorp.onlinevoting.model.Vote;
import com.evilcorp.onlinevoting.model.Voting;
import com.evilcorp.onlinevoting.model.VotingUser;
import com.evilcorp.onlinevoting.model.enums.VotingStatus;
import com.evilcorp.onlinevoting.repository.VoteRepository;
import com.evilcorp.onlinevoting.exception.DuplicateVoteException;
import com.evilcorp.onlinevoting.exception.VotingRuleException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class VoteAttempt {
    private Voting voting;
    private VotingUser user;
    private Candidate candidate;

    private final VoteRepository voteRepository;

    public VoteAttempt prepare(@NonNull Voting voting, @NonNull Candidate candidate, @NonNull VotingUser user) {
        if (this.voting != null) {
            throw new VotingRuleException("error.attempt.already-prepared", "Vote attempt already prepared");
        }
        this.voting = voting;
        this.candidate = candidate;
        this.user = user;
        return this;
    }

    public Vote toVote() {
        if (voting == null) {
            throw new VotingRuleException("error.attempt.not-prepared", "Vote attempt was not prepared");
        }

        if (voting.getStatus() != VotingStatus.ACTIVE) {
            throw new VotingRuleException("error.attempt.inactive", "Voting is not active: " + voting.getId());
        }

        if (!candidate.getVoting().getId().equals(voting.getId())) {
            throw new VotingRuleException(
                    "error.attempt.wrong-candidate",
                    "Candidate " + candidate.getId() + " does not belong to voting " + voting.getId()
            );
        }

        if (voteRepository.existsByVotingIdAndUserId(voting.getId(), user.getId())) {
            throw new DuplicateVoteException("User " + user.getId() + " has already voted in " + voting.getId());
        }

        return new Vote(voting, candidate, user);
    }

}
