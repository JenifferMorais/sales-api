-- Tabela para armazenar tokens JWT invalidados (logout)
CREATE TABLE token_blacklist (
    id BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    blacklisted_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

-- Índice para busca rápida por hash do token
CREATE INDEX idx_token_hash ON token_blacklist(token_hash);

-- Índice para limpeza de tokens expirados
CREATE INDEX idx_expires_at ON token_blacklist(expires_at);

-- Comentários
COMMENT ON TABLE token_blacklist IS 'Armazena tokens JWT invalidados através de logout';
COMMENT ON COLUMN token_blacklist.token_hash IS 'Hash SHA-256 do token JWT';
COMMENT ON COLUMN token_blacklist.user_id IS 'ID do usuário que fez logout';
COMMENT ON COLUMN token_blacklist.blacklisted_at IS 'Data e hora em que o token foi invalidado';
COMMENT ON COLUMN token_blacklist.expires_at IS 'Data e hora de expiração original do token';
