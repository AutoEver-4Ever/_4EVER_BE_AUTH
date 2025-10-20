-- Spring Authorization Server schema for PostgreSQL
-- Create tables if they do not exist to allow repeated app starts.

-- Registered Clients
CREATE TABLE IF NOT EXISTS oauth2_registered_client (
    id varchar(100) PRIMARY KEY,
    client_id varchar(100) NOT NULL,
    client_id_issued_at timestamp with time zone,
    client_secret varchar(200),
    client_secret_expires_at timestamp with time zone,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000),
    post_logout_redirect_uris varchar(1000),
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_oauth2_registered_client_client_id
    ON oauth2_registered_client (client_id);

-- Authorizations
CREATE TABLE IF NOT EXISTS oauth2_authorization (
    id varchar(100) PRIMARY KEY,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000),
    attributes text,
    state varchar(500),
    authorization_code_value text,
    authorization_code_issued_at timestamp with time zone,
    authorization_code_expires_at timestamp with time zone,
    authorization_code_metadata text,
    access_token_value text,
    access_token_issued_at timestamp with time zone,
    access_token_expires_at timestamp with time zone,
    access_token_metadata text,
    access_token_type varchar(100),
    access_token_scopes varchar(1000),
    oidc_id_token_value text,
    oidc_id_token_issued_at timestamp with time zone,
    oidc_id_token_expires_at timestamp with time zone,
    oidc_id_token_metadata text,
    refresh_token_value text,
    refresh_token_issued_at timestamp with time zone,
    refresh_token_expires_at timestamp with time zone,
    refresh_token_metadata text
);

CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_state
    ON oauth2_authorization (state);
CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_authorization_code_value
    ON oauth2_authorization (authorization_code_value);
CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_access_token_value
    ON oauth2_authorization (access_token_value);
CREATE INDEX IF NOT EXISTS idx_oauth2_authorization_refresh_token_value
    ON oauth2_authorization (refresh_token_value);

-- Authorization Consents
CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

