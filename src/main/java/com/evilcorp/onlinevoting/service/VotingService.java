package com.evilcorp.onlinevoting.service;

import java.util.List;
import java.util.UUID;
import java.util.Comparator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.evilcorp.onlinevoting.model.Vote;
import com.evilcorp.onlinevoting.model.Voting;
import com.evilcorp.onlinevoting.model.Candidate;
import com.evilcorp.onlinevoting.model.VotingUser;
import com.evilcorp.onlinevoting.config.VotingLinks;
import com.evilcorp.onlinevoting.model.enums.VotingStatus;
import com.evilcorp.onlinevoting.repository.VoteRepository;
import com.evilcorp.onlinevoting.mapper.VotingDetailsMapper;
import com.evilcorp.onlinevoting.dto.request.CastVoteRequest;
import com.evilcorp.onlinevoting.repository.VotingRepository;
import com.evilcorp.onlinevoting.exception.VotingRuleException;
import com.evilcorp.onlinevoting.dto.request.OwnerActionRequest;
import com.evilcorp.onlinevoting.dto.request.CreateVotingRequest;
import com.evilcorp.onlinevoting.dto.request.UpdateVotingRequest;
import com.evilcorp.onlinevoting.exception.VotingAccessException;
import com.evilcorp.onlinevoting.repository.VotingUserRepository;
import com.evilcorp.onlinevoting.dto.response.VotingDetailsResponse;
import com.evilcorp.onlinevoting.dto.response.VotingSummaryResponse;
import com.evilcorp.onlinevoting.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class VotingService {

    private final VoteRepository voteRepository;
    private final VotingRepository votingRepository;
    private final VotingUserRepository userRepository;
    private final VotingDetailsMapper votingDetailsMapper;

    @Autowired
    private VotingLinks votingLinks;

    @Transactional
    public UUID createVoting(CreateVotingRequest request) {
        VotingUser owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("error.owner.not-found",
                        "Voting owner not found: " + request.getOwnerId()));

        String normalizedTitle = request.getTitle().strip();

        List<String> names = request.getCandidateNames().stream()
                .map(String::strip)
                .filter(name -> !name.isBlank())
                .toList();

        Voting voting = new Voting(normalizedTitle, owner);
        names.forEach(voting::addCandidate);
        return votingRepository.save(voting).getId();
    }

    @Transactional
    public void updateVoting(UpdateVotingRequest request) {
        Voting voting = votingForUpdate(request.getVotingId());
        requireOwner(voting, request.getUserId());

        if (voting.getStatus() != VotingStatus.DRAFT) {
            throw new VotingRuleException(
                    "error.voting.edit.not-draft", "Only draft voting can be edited: " + request.getVotingId()
            );
        }

        List<String> candidateNames = request.getCandidateNames().stream()
                .map(String::strip)
                .filter(name -> !name.isBlank())
                .toList();

        voting.update(request.getTitle().strip(), candidateNames);
        votingRepository.flush();
    }

    @Transactional
    public void startVoting(OwnerActionRequest request) {
        Voting voting = votingForUpdate(request.getVotingId());
        requireOwner(voting, request.getUserId());

        if (voting.getCandidates().isEmpty()) {
            throw new VotingRuleException(
                    "error.voting.no-candidates", "Cannot start voting without candidates: " + request.getVotingId()
            );
        }

        VotingStatus nextStatus = switch (voting.getStatus()) {
            case DRAFT -> VotingStatus.ACTIVE;
            case ACTIVE -> throw new VotingRuleException(
                    "error.voting.already-active", "Voting already active: " + request.getVotingId()
            );
            case STOPPED -> throw new VotingRuleException(
                    "error.voting.cannot-restart", "Cannot restart stopped voting: " + request.getVotingId()
            );
        };

        voting.changeStatus(nextStatus);
    }

    @Transactional
    public void stopVoting(OwnerActionRequest request) {
        Voting voting = votingForUpdate(request.getVotingId());

        requireOwner(voting, request.getUserId());

        VotingStatus nextStatus = switch (voting.getStatus()) {
            case ACTIVE -> VotingStatus.STOPPED;
            case DRAFT -> throw new VotingRuleException(
                    "error.voting.not-started", "Voting has not started: " + request.getVotingId()
            );
            case STOPPED -> throw new VotingRuleException(
                    "error.voting.already-stopped", "Voting already stopped: " + request.getVotingId()
            );
        };

        voting.changeStatus(nextStatus);
    }

    @Transactional
    public UUID castVote(CastVoteRequest request) {
        Voting voting = votingForUpdate(request.getVotingId());

        Candidate candidate = voting.getCandidates().stream()
                .filter(item -> item.getId().equals(request.getCandidateId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "error.candidate.not-in-voting",
                        "Candidate " + request.getCandidateId() + " not found in voting " + request.getVotingId()
                ));

        VotingUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "error.user.not-found", "Voting user not found: " + request.getUserId()
                ));

        Vote vote = newVoteAttempt().prepare(voting, candidate, user).toVote();

        try {
            return voteRepository.saveAndFlush(vote).getId();
        } catch (DataIntegrityViolationException exception) {
            throw new VotingRuleException(
                    "error.vote.not-saved",
                    "Vote could not be saved for user " + request.getUserId() + " in voting " + request.getVotingId(), exception
            );
        }

    }

    @Transactional(readOnly = true)
    public List<VotingSummaryResponse> listVotings() {
        return votingRepository.findAll().stream()
                .map(votingDetailsMapper::toSummaryResponse)
                .sorted(Comparator.comparing(VotingSummaryResponse::title))
                .toList();
    }

    @Transactional(readOnly = true)
    public VotingDetailsResponse getDetails(UUID votingId) {
        Voting voting = requireVoting(votingId);

        var candidates = voting.getCandidates().stream()
                .sorted(Comparator.comparing(Candidate::getName))
                .map(votingDetailsMapper::toCandidateResponse)
                .toList();

        return votingDetailsMapper.toResponse(voting, candidates);
    }

    @Transactional(readOnly = true)
    public String getVotingUrl(UUID votingId) {
        return votingLinks.votingUrl(requireVoting(votingId).getId());
    }

    @Transactional(readOnly = true)
    public String getResultsUrl(UUID votingId) {
        return votingLinks.resultsUrl(requireVoting(votingId).getId());
    }

    private Voting requireVoting(UUID votingId) {
        return votingRepository.findById(votingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "error.voting.not-found", "Voting not found: " + votingId
                ));
    }

    private Voting votingForUpdate(UUID votingId) {
        return votingRepository.findByIdForUpdate(votingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "error.voting.not-found", "Voting not found: " + votingId
                ));
    }

    private void requireOwner(Voting voting, UUID ownerId) {
        if (!voting.getOwner().getId().equals(ownerId)) {
            throw new VotingAccessException("User " + ownerId + " cannot manage voting " + voting.getId());
        }
    }

    @Lookup
    protected VoteAttempt newVoteAttempt() {
        throw new IllegalStateException("Spring @Lookup is not configured");
    }

}
