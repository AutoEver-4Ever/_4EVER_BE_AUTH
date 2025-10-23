-- Initial schema for authorization server and RBAC tables

CREATE TABLE oauth2_registered_client
(
    id                            varchar(100) PRIMARY KEY,
    client_id                     varchar(100)  NOT NULL,
    client_id_issued_at           timestamp with time zone,
    client_secret                 varchar(200),
    client_secret_expires_at      timestamp with time zone,
    client_name                   varchar(200)  NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types     varchar(1000) NOT NULL,
    redirect_uris                 varchar(1000),
    post_logout_redirect_uris     varchar(1000),
    scopes                        varchar(1000) NOT NULL,
    client_settings               varchar(2000) NOT NULL,
    token_settings                varchar(2000) NOT NULL
);

CREATE UNIQUE INDEX idx_oauth2_registered_client_client_id
    ON oauth2_registered_client (client_id);

CREATE TABLE oauth2_authorization
(
    id                            varchar(100) PRIMARY KEY,
    registered_client_id          varchar(100) NOT NULL,
    principal_name                varchar(200) NOT NULL,
    authorization_grant_type      varchar(100) NOT NULL,
    authorized_scopes             varchar(1000),
    attributes                    text,
    state                         varchar(500),
    authorization_code_value      text,
    authorization_code_issued_at  timestamp with time zone,
    authorization_code_expires_at timestamp with time zone,
    authorization_code_metadata   text,
    access_token_value            text,
    access_token_issued_at        timestamp with time zone,
    access_token_expires_at       timestamp with time zone,
    access_token_metadata         text,
    access_token_type             varchar(100),
    access_token_scopes           varchar(1000),
    oidc_id_token_value           text,
    oidc_id_token_issued_at       timestamp with time zone,
    oidc_id_token_expires_at      timestamp with time zone,
    oidc_id_token_metadata        text,
    refresh_token_value           text,
    refresh_token_issued_at       timestamp with time zone,
    refresh_token_expires_at      timestamp with time zone,
    refresh_token_metadata        text
);

CREATE INDEX idx_oauth2_authorization_state
    ON oauth2_authorization (state);

CREATE INDEX idx_oauth2_authorization_authorization_code_value
    ON oauth2_authorization (authorization_code_value);

CREATE INDEX idx_oauth2_authorization_access_token_value
    ON oauth2_authorization (access_token_value);

CREATE INDEX idx_oauth2_authorization_refresh_token_value
    ON oauth2_authorization (refresh_token_value);

CREATE TABLE oauth2_authorization_consent
(
    registered_client_id varchar(100)  NOT NULL,
    principal_name       varchar(200)  NOT NULL,
    authorities          varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE users
(
    user_id                  uuid PRIMARY KEY,
    login_email              varchar(255) NOT NULL UNIQUE,
    contact_email            varchar(255),
    username                 varchar(100) NOT NULL,
    password                 varchar(100) NOT NULL,
    phone_number             varchar(30),
    user_type                varchar(20)  NOT NULL,
    status                   varchar(20)  NOT NULL,
    password_last_changed_at timestamp with time zone,
    last_login_at            timestamp with time zone,
    created_at               timestamp with time zone DEFAULT now(),
    updated_at               timestamp with time zone DEFAULT now(),
    CONSTRAINT chk_users_user_type CHECK (user_type IN ('INTERNAL', 'CUSTOMER', 'SUPPLIER')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED'))
);

CREATE TABLE modules
(
    module_id   uuid PRIMARY KEY,
    code        varchar(30)  NOT NULL UNIQUE,
    name        varchar(100) NOT NULL,
    description text
);

CREATE TABLE permissions
(
    permission_id uuid PRIMARY KEY,
    module_id     uuid         NOT NULL,
    code          varchar(100) NOT NULL,
    action        varchar(30)  NOT NULL,
    resource      varchar(100),
    CONSTRAINT fk_permissions_module FOREIGN KEY (module_id) REFERENCES modules (module_id) ON DELETE RESTRICT,
    CONSTRAINT uq_permissions_module_code UNIQUE (module_id, code)
);

CREATE TABLE roles
(
    role_id     uuid PRIMARY KEY,
    code        varchar(100) NOT NULL UNIQUE,
    name        varchar(100) NOT NULL,
    description text
);

CREATE TABLE role_permissions
(
    role_id       uuid NOT NULL,
    permission_id uuid NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_perm FOREIGN KEY (permission_id) REFERENCES permissions (permission_id) ON DELETE CASCADE
);

CREATE TABLE user_roles
(
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
);
