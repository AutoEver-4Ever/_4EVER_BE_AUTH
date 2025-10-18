package org.ever._4ever_be_auth.auth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JpaRegisteredClientRepositoryAdapter implements RegisteredClientRepository {

    private final RegisteredClientJpaRepository repository;

    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {
        RegisteredClientJpaEntity entity = repository.findById(registeredClient.getId())
                .orElseGet(RegisteredClientJpaEntity::new);
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt() != null
                ? registeredClient.getClientIdIssuedAt()
                : Instant.now());
        entity.setRegisteredClient(registeredClient);
        repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public RegisteredClient findById(String id) {
        return repository.findById(id)
                .map(RegisteredClientJpaEntity::getRegisteredClient)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public RegisteredClient findByClientId(String clientId) {
        return repository.findByClientId(clientId)
                .map(RegisteredClientJpaEntity::getRegisteredClient)
                .orElse(null);
    }
}
