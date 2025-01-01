-- Создание таблицы ролей
CREATE TABLE roles (
                       id UUID PRIMARY KEY,
                       role VARCHAR(50) NOT NULL UNIQUE
);

-- Создание таблицы пользователей
CREATE TABLE persons (
                         id UUID PRIMARY KEY,
                         username VARCHAR(50) NOT NULL,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         bank_id UUID NOT NULL,
                         role_id UUID NOT NULL
);
ALTER TABLE persons ADD CONSTRAINT fk_persons_banks FOREIGN KEY (bank_id) REFERENCES banks(id);
ALTER TABLE persons ADD CONSTRAINT fk_persons_roles FOREIGN KEY (role_id) REFERENCES roles(id);