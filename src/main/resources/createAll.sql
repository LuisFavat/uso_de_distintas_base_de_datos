CREATE TABLE IF NOT EXISTS patogeno (
    id BIGINT AUTO_INCREMENT NOT NULL,
    tipo VARCHAR(255) NOT NULL UNIQUE,
    cantidadDeEspecies int,
    PRIMARY KEY (id)

);