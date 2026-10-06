CREATE TABLE identity.authentication_identities (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    user_id BIGINT NOT NULL,

    provider VARCHAR(30) NOT NULL,
    provider_subject VARCHAR(255) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_authenticated_at TIMESTAMPTZ,

    CONSTRAINT fk_auth_identity_user
        FOREIGN KEY (user_id)
        REFERENCES identity.users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_auth_identity_provider
        CHECK (provider IN ('GOOGLE', 'PHONE_OTP')),

    CONSTRAINT uq_auth_identity_provider_subject
        UNIQUE (provider, provider_subject),

    CONSTRAINT uq_auth_identity_user_provider
        UNIQUE (user_id, provider)
);
