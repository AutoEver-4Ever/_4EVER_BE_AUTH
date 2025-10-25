package org.ever._4ever_be_auth.auth.oauth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.auth.oauth.entity.RegisteredClientEntity;
import org.ever._4ever_be_auth.auth.oauth.repository.RegisteredClientJpaRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    private final RegisteredClientJpaRepository jpaRepository;
    private final RegisteredClientMapper mapper;

    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {
        RegisteredClientEntity entity = mapper.toEntity(registeredClient);
        Optional<RegisteredClientEntity> existing = jpaRepository.findById(entity.getId());
        if (existing.isPresent()) {
            RegisteredClientEntity target = existing.get();
            target.updateFrom(entity);
        } else {
            jpaRepository.save(entity);
        }
    }

    @Override
    public RegisteredClient findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toRegisteredClient)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return jpaRepository.findByClientId(clientId)
                .map(mapper::toRegisteredClient)
                .orElse(null);
    }
}
