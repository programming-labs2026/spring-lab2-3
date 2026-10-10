package com.evilcorp.onlinevoting.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.evilcorp.onlinevoting.model.Voting;
import com.evilcorp.onlinevoting.model.Candidate;
import com.evilcorp.onlinevoting.dto.response.VotingDetailsResponse;
import com.evilcorp.onlinevoting.dto.response.VotingSummaryResponse;
import com.evilcorp.onlinevoting.dto.response.CandidateOptionResponse;

@Mapper
public interface VotingDetailsMapper {
    @Mapping(target = "id", source = "voting.id")
    @Mapping(target = "title", source = "voting.title")
    @Mapping(target = "ownerId", source = "voting.owner.id")
    @Mapping(target = "ownerName", source = "voting.owner.username")
    @Mapping(target = "status", source = "voting.status")
    @Mapping(target = "candidates", source = "candidates")
    VotingDetailsResponse toResponse(Voting voting, List<CandidateOptionResponse> candidates);

    CandidateOptionResponse toCandidateResponse(Candidate candidate);

    @Mapping(target = "ownerName", source = "owner.username")
    VotingSummaryResponse toSummaryResponse(Voting voting);
}
