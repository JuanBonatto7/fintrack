CREATE DATABASE IF NOT EXISTS fintrack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fintrack;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    icon VARCHAR(100),
    color VARCHAR(30),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    user_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DECIMAL(19,2) NOT NULL,
    description VARCHAR(255),
    date DATE NOT NULL,
    type VARCHAR(20) NOT NULL,
    account_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DECIMAL(19,2) NOT NULL,
    budget_month INT NOT NULL,
    budget_year INT NOT NULL,
    account_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_budgets_period (account_id, category_id, budget_year, budget_month),
    CONSTRAINT fk_budgets_account FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT fk_budgets_category FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB;

INSERT INTO categories (name, type, icon, color)
SELECT 'Salary', 'INCOME', 'S', '#7450b5' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Salary' AND type = 'INCOME');
INSERT INTO categories (name, type, icon, color)
SELECT 'Freelance', 'INCOME', 'F', '#8c68c7' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Freelance' AND type = 'INCOME');
INSERT INTO categories (name, type, icon, color)
SELECT 'Investments', 'INCOME', 'I', '#b08dde' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Investments' AND type = 'INCOME');
INSERT INTO categories (name, type, icon, color)
SELECT 'Food & dining', 'EXPENSE', 'FD', '#d978a2' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Food & dining' AND type = 'EXPENSE');
INSERT INTO categories (name, type, icon, color)
SELECT 'Housing', 'EXPENSE', 'H', '#8b9fd2' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Housing' AND type = 'EXPENSE');
INSERT INTO categories (name, type, icon, color)
SELECT 'Transport', 'EXPENSE', 'T', '#72b88a' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Transport' AND type = 'EXPENSE');
INSERT INTO categories (name, type, icon, color)
SELECT 'Health', 'EXPENSE', 'H', '#d9a46f' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Health' AND type = 'EXPENSE');
INSERT INTO categories (name, type, icon, color)
SELECT 'Entertainment', 'EXPENSE', 'E', '#d28ac0' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Entertainment' AND type = 'EXPENSE');
INSERT INTO categories (name, type, icon, color)
SELECT 'Shopping', 'EXPENSE', 'S', '#c88973' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Shopping' AND type = 'EXPENSE');
INSERT INTO categories (name, type, icon, color)
SELECT 'Other', 'BOTH', 'O', '#9b91a3' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Other' AND type = 'BOTH');

SELECT TABLE_NAME
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'fintrack'
ORDER BY TABLE_NAME;

SELECT TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'fintrack'
  AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, COLUMN_NAME;
