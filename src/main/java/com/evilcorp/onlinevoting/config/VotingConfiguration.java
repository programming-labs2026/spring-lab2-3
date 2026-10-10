package com.evilcorp.onlinevoting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

@Configuration
public class VotingConfiguration {
    @Value("${voting.public-base-url:http://localhost:8081/online-voting}")
    private String baseUrl;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public VotingLinks votingLinks() {
        return new VotingLinks(baseUrl);
    }
}
