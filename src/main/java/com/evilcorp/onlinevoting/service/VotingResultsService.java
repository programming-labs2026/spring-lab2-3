package com.evilcorp.onlinevoting.service;

import java.util.Map;
import java.util.UUID;
import java.util.Comparator;
import java.util.stream.Collectors;

import lombok.Setter;
import lombok.RequiredArgsConstructor;

import com.evilcorp.onlinevoting.model.Voting;
import com.evilcorp.onlinevoting.model.Candidate;
import com.evilcorp.onlinevoting.mapper.VotingResultsMapper;
import com.evilcorp.onlinevoting.dto.response.VotingResultsResponse;
import com.evilcorp.onlinevoting.exception.ResourceNotFoundException;

import com.evilcorp.onlinevoting.repository.VoteRepository;
import com.evilcorp.onlinevoting.repository.VotingRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VotingResultsService {

    @Setter(onMethod_ = @Autowired)
    private VoteRepository voteRepository;

    private final VotingRepository votingRepository;
    private final VotingResultsMapper votingResultsMapper;

    @Transactional(readOnly = true)
    public VotingResultsResponse getResults(UUID votingId) {
        Voting voting = votingRepository.findById(votingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "error.voting.not-found",
                        "Voting not found: " + votingId
                ));

        Map<UUID, Long> counts = voteRepository.countVotesByVotingId(votingId).stream()
                .collect(Collectors.toMap(
                        VoteRepository.CandidateVoteCount::getCandidateId,
                        VoteRepository.CandidateVoteCount::getVotes
                ));

        var candidates = voting.getCandidates().stream()
                .sorted(Comparator.comparing(Candidate::getName))
                .map(candidate -> votingResultsMapper.toCandidateResponse(
                        candidate, counts.getOrDefault(candidate.getId(), 0L))
                )
                .toList();

        long totalVotes = counts.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        return votingResultsMapper.toResponse(voting, candidates, totalVotes);
    }


}
