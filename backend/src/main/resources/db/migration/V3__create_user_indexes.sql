CREATE UNIQUE INDEX uq_users_email_normalized
    ON identity.users (LOWER(email))
    WHERE email IS NOT NULL;

CREATE UNIQUE INDEX uq_users_phone_e164
    ON identity.users (phone_e164)
    WHERE phone_e164 IS NOT NULL;
