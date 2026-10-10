package com.evilcorp.onlinevoting.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import com.evilcorp.onlinevoting.model.Voting;
import com.evilcorp.onlinevoting.model.Candidate;
import com.evilcorp.onlinevoting.dto.response.VotingResultsResponse;
import com.evilcorp.onlinevoting.dto.response.CandidateResultResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VotingResultsMapper {
    @Mapping(target = "votingId", source = "voting.id")
    @Mapping(target = "title", source = "voting.title")
    @Mapping(target = "status", source = "voting.status")
    @Mapping(target = "candidates", source = "candidates")
    @Mapping(target = "totalVotes", source = "totalVotes")
    VotingResultsResponse toResponse(Voting voting, List<CandidateResultResponse> candidates, long totalVotes);

    @Mapping(target = "id", source = "candidate.id")
    @Mapping(target = "name", source = "candidate.name")
    @Mapping(target = "votes", source = "votes")
    CandidateResultResponse toCandidateResponse(Candidate candidate, long votes);
}
