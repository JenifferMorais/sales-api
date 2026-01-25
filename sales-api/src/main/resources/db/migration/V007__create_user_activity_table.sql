-- Tabela para rastrear última atividade de usuários (timeout de inatividade)
CREATE TABLE user_activity (
    id BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    last_activity_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Índice para busca rápida por hash do token
CREATE INDEX idx_token_hash_activity ON user_activity(token_hash);

-- Índice para limpeza de atividades antigas
CREATE INDEX idx_last_activity ON user_activity(last_activity_at);

-- Comentários
COMMENT ON TABLE user_activity IS 'Rastreia última atividade de usuários para timeout de inatividade';
COMMENT ON COLUMN user_activity.token_hash IS 'Hash SHA-256 do token JWT';
COMMENT ON COLUMN user_activity.user_id IS 'ID do usuário';
COMMENT ON COLUMN user_activity.last_activity_at IS 'Timestamp da última requisição do usuário';
COMMENT ON COLUMN user_activity.created_at IS 'Data e hora em que o token foi usado pela primeira vez';
