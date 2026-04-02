CREATE TABLE IF NOT EXISTS auth.users (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    cpf          VARCHAR(11)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    active       BOOLEAN      NOT NULL DEFAULT true,
    created_at   TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT now()
);
