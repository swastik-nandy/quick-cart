CREATE TABLE identity.user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(30) NOT NULL,

    CONSTRAINT pk_user_roles
        PRIMARY KEY (user_id, role),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES identity.users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_user_roles_role
        CHECK (role IN ('CUSTOMER', 'ADMIN'))
);

INSERT INTO identity.user_roles (user_id, role)
SELECT id, role
FROM identity.users;

ALTER TABLE identity.users
    DROP CONSTRAINT chk_users_role;

ALTER TABLE identity.users
    DROP COLUMN role;

UPDATE identity.authentication_identities
SET provider = 'PHONE'
WHERE provider = 'PHONE_OTP';

ALTER TABLE identity.authentication_identities
    DROP CONSTRAINT chk_auth_identity_provider;

ALTER TABLE identity.authentication_identities
    ADD CONSTRAINT chk_auth_identity_provider
        CHECK (provider IN ('GOOGLE', 'PHONE'));
