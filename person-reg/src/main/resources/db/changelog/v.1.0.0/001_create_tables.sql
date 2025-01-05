-- Создание таблицы банков
CREATE TABLE banks (
                       id UUID PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       country VARCHAR(50) NOT NULL
);

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
                         role_id UUID NOT NULL
);
ALTER TABLE persons ADD CONSTRAINT fk_persons_roles FOREIGN KEY (role_id) REFERENCES roles(id);

-- Создание таблицы категорий
CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name VARCHAR(50) NOT NULL
);

-- Создание таблицы доходов
CREATE TABLE incomes (
                         id UUID PRIMARY KEY,
                         person_id UUID NOT NULL,
                         amount DECIMAL(10,2) NOT NULL,
                         description TEXT,
                         bank_id UUID NOT NULL,
                         date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE incomes ADD CONSTRAINT fk_incomes_persons FOREIGN KEY (person_id) REFERENCES persons(id);
ALTER TABLE incomes ADD CONSTRAINT fk_incomes_banks FOREIGN KEY (bank_id) REFERENCES banks(id);

-- Создание таблицы расходов
CREATE TABLE expenses (
                          id UUID PRIMARY KEY,
                          person_id UUID NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          category_id UUID NOT NULL,
                          description TEXT,
                          bank_id UUID NOT NULL,
                          date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE expenses ADD CONSTRAINT fk_expenses_persons FOREIGN KEY (person_id) REFERENCES persons(id);
ALTER TABLE expenses ADD CONSTRAINT fk_expenses_categories FOREIGN KEY (category_id) REFERENCES categories(id);
ALTER TABLE expenses ADD CONSTRAINT fk_expenses_banks FOREIGN KEY (bank_id) REFERENCES banks(id);