package com.evilcorp.onlinevoting.service;

import java.util.List;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evilcorp.onlinevoting.mapper.VotingUserMapper;
import com.evilcorp.onlinevoting.repository.VotingUserRepository;
import com.evilcorp.onlinevoting.dto.response.UserOptionResponse;

@Service
@RequiredArgsConstructor
public class VotingUserService {
    private final VotingUserMapper userMapper;
    private final VotingUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserOptionResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .sorted(Comparator.comparing(UserOptionResponse::username))
                .toList();
    }
}
