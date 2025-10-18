package org.ever._4ever_be_auth.auth.client;

import jakarta.persistence.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.time.Instant;

@Entity
@Table(name = "registered_client")
public class RegisteredClientJpaEntity {

    @Id
    private String id;

    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @Column(name = "client_id_issued_at")
    private Instant clientIdIssuedAt;

    @Lob
    @Column(name = "client_metadata", nullable = false)
    @Convert(converter = RegisteredClientAttributeConverter.class)
    private RegisteredClient registeredClient;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Instant getClientIdIssuedAt() {
        return clientIdIssuedAt;
    }

    public void setClientIdIssuedAt(Instant clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
    }

    public RegisteredClient getRegisteredClient() {
        return registeredClient;
    }

    public void setRegisteredClient(RegisteredClient registeredClient) {
        this.registeredClient = registeredClient;
    }
}
