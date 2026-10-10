package com.evilcorp.onlinevoting.mapper;

import org.mapstruct.Mapper;
import com.evilcorp.onlinevoting.model.VotingUser;
import com.evilcorp.onlinevoting.dto.response.UserOptionResponse;

@Mapper
public interface VotingUserMapper {
    UserOptionResponse toResponse(VotingUser user);
}
