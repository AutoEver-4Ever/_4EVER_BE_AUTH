package org.ever._4ever_be_auth.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    boolean existsByLoginEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByLoginEmail(String email);
}
