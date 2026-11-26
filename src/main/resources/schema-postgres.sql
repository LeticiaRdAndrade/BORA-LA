-- Uma viagem tem vários pontos turísticos
CREATE TABLE IF NOT EXISTS viagem(
    id SERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    cidade VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS ponto(
    id SERIAL PRIMARY KEY,
    viagem_id INT NOT NULL REFERENCES viagem(id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    ordem INT NOT NULL,                    -- posição do ponto no roteiro
    visitado BOOLEAN NOT NULL DEFAULT FALSE
);
