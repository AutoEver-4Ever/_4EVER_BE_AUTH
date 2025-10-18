package org.ever._4ever_be_auth.auth.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

/**
 * Persists {@link RegisteredClient} as JSON using JPA.
 */
@Converter(autoApply = false)
public class RegisteredClientAttributeConverter implements AttributeConverter<RegisteredClient, String> {

    private final ObjectMapper objectMapper;

    public RegisteredClientAttributeConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public String convertToDatabaseColumn(RegisteredClient attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize RegisteredClient", ex);
        }
    }

    @Override
    public RegisteredClient convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, RegisteredClient.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize RegisteredClient", ex);
        }
    }
}
