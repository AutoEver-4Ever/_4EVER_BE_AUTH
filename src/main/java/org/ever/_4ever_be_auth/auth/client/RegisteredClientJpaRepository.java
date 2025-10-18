package org.ever._4ever_be_auth.auth.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisteredClientJpaRepository extends JpaRepository<RegisteredClientJpaEntity, String> {

    Optional<RegisteredClientJpaEntity> findByClientId(String clientId);
}
