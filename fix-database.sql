-- fix-database.sql
-- Полное пересоздание базы данных с правильными паролями

DROP DATABASE IF EXISTS insurance_system;
CREATE DATABASE insurance_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE insurance_system;

-- 1. Таблица пользователей с РАБОЧИМИ паролями
CREATE TABLE system_users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email_address VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_user_name VARCHAR(150) NOT NULL,
    user_role VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    account_active BOOLEAN DEFAULT TRUE,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ГАРАНТИРОВАННО рабочий BCrypt хэш для password123
INSERT INTO system_users (email_address, password, full_user_name, user_role, phone_number) VALUES
('admin@insurance.ru', '$2a$10$w6HDrIQOA/mc25.KmQl9x.IIl4NYBX6HkL5v.Cn.HWR0B4A3xAcT2', 'Петров Пётр Петрович', 'ROLE_ADMIN', '+7 (495) 123-45-67'),
('ivanov@insurance.ru', '$2a$10$w6HDrIQOA/mc25.KmQl9x.IIl4NYBX6HkL5v.Cn.HWR0B4A3xAcT2', 'Иванов Иван Иванович', 'ROLE_MANAGER', '+7 (495) 234-56-78'),
('sidorov@insurance.ru', '$2a$10$w6HDrIQOA/mc25.KmQl9x.IIl4NYBX6HkL5v.Cn.HWR0B4A3xAcT2', 'Сидоров Сидор Сидорович', 'ROLE_MANAGER', '+7 (495) 345-67-89'),
('smirnov@insurance.ru', '$2a$10$w6HDrIQOA/mc25.KmQl9x.IIl4NYBX6HkL5v.Cn.HWR0B4A3xAcT2', 'Смирнов Алексей Михайлович', 'ROLE_AGENT', '+7 (495) 456-78-90'),
('kozlov@mail.ru', '$2a$10$w6HDrIQOA/mc25.KmQl9x.IIl4NYBX6HkL5v.Cn.HWR0B4A3xAcT2', 'Козлов Андрей Викторович', 'ROLE_CLIENT', '+7 (999) 111-22-33'),
('novikov@mail.ru', '$2a$10$w6HDrIQOA/mc25.KmQl9x.IIl4NYBX6HkL5v.Cn.HWR0B4A3xAcT2', 'Новиков Дмитрий Сергеевич', 'ROLE_CLIENT', '+7 (999) 222-33-44'),
('fedorov@mail.ru', '$2a$10$w6HDrIQOA/mc25.KmQl9x.IIl4NYBX6HkL5v.Cn.HWR0B4A3xAcT2', 'Фёдоров Михаил Александрович', 'ROLE_CLIENT', '+7 (999) 333-44-55');

-- 2. Остальные таблицы
CREATE TABLE insurance_types (
    insurance_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(100) UNIQUE NOT NULL,
    type_description TEXT,
    base_rate_percent DECIMAL(5,2) NOT NULL,
    is_active_type BOOLEAN DEFAULT TRUE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE clients (
    client_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_client_name VARCHAR(150) NOT NULL,
    birth_date DATE NOT NULL,
    passport_series VARCHAR(10),
    passport_number VARCHAR(20) NOT NULL,
    client_address VARCHAR(255),
    phone_number VARCHAR(20) NOT NULL,
    email_address VARCHAR(100),
    inn_number VARCHAR(12),
    manager_id BIGINT,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manager_id) REFERENCES system_users(user_id)
);

CREATE TABLE insurance_policies (
    policy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_number VARCHAR(50) UNIQUE NOT NULL,
    client_id BIGINT NOT NULL,
    insurance_type_id BIGINT NOT NULL,
    policy_start_date DATE NOT NULL,
    policy_end_date DATE NOT NULL,
    insured_sum_amount DECIMAL(15,2) NOT NULL,
    premium_payment_amount DECIMAL(15,2) NOT NULL,
    policy_status VARCHAR(50) NOT NULL,
    manager_user_id BIGINT,
    policy_notes TEXT,
    policy_created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    policy_updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(client_id),
    FOREIGN KEY (insurance_type_id) REFERENCES insurance_types(insurance_type_id),
    FOREIGN KEY (manager_user_id) REFERENCES system_users(user_id)
);

CREATE TABLE insurance_claims (
    claim_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    claim_number VARCHAR(50) UNIQUE NOT NULL,
    incident_occurrence_date DATE NOT NULL,
    claim_report_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    incident_description TEXT NOT NULL,
    claimed_compensation_amount DECIMAL(15,2) NOT NULL,
    approved_compensation_amount DECIMAL(15,2),
    claim_status VARCHAR(50) NOT NULL,
    processing_manager_id BIGINT,
    resolution_decision_date DATE,
    payment_execution_date DATE,
    additional_notes TEXT,
    claim_updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (policy_id) REFERENCES insurance_policies(policy_id),
    FOREIGN KEY (processing_manager_id) REFERENCES system_users(user_id)
);

CREATE TABLE policy_payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    payment_execution_date DATE NOT NULL,
    payment_amount DECIMAL(15,2) NOT NULL,
    payment_type VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50),
    transaction_reference_id VARCHAR(100),
    payment_status VARCHAR(50) NOT NULL,
    payment_notes TEXT,
    payment_created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (policy_id) REFERENCES insurance_policies(policy_id)
);

-- 3. Вставка данных
INSERT INTO insurance_types (type_name, type_description, base_rate_percent, is_active_type) VALUES
('ОСАГО', 'Обязательное страхование автогражданской ответственности', 3.50, TRUE),
('КАСКО', 'Добровольное страхование транспортных средств', 7.00, TRUE),
('ДМС', 'Добровольное медицинское страхование', 5.00, TRUE),
('Страхование недвижимости', 'Защита квартир, домов и коммерческой недвижимости', 2.50, TRUE),
('Страхование жизни', 'Накопительное и рисковое страхование жизни', 4.00, TRUE);

INSERT INTO clients (full_client_name, birth_date, passport_series, passport_number, client_address, phone_number, email_address, inn_number, manager_id) VALUES
('Козлов Андрей Викторович', '1988-12-10', '4514', '111222', 'г. Москва, ул. Ленина, д. 5, кв. 12', '+7 (999) 111-22-33', 'kozlov@mail.ru', '771234567890', 2),
('Новиков Дмитрий Сергеевич', '1992-05-15', '4515', '222333', 'г. Москва, ул. Пушкина, д. 10, кв. 25', '+7 (999) 222-33-44', 'novikov@mail.ru', '772345678901', 2),
('Фёдоров Михаил Александрович', '1985-08-20', '4516', '333444', 'г. Москва, ул. Гагарина, д. 7, кв. 8', '+7 (999) 333-44-55', 'fedorov@mail.ru', '773456789012', 3);

INSERT INTO insurance_policies (policy_number, client_id, insurance_type_id, policy_start_date, policy_end_date, insured_sum_amount, premium_payment_amount, policy_status, manager_user_id, policy_notes) VALUES
('POL-2024-OSAGO001', 1, 1, '2024-01-15', '2025-01-14', 500000.00, 17500.00, 'ACTIVE', 2, 'Полис ОСАГО на автомобиль Hyundai Solaris'),
('POL-2024-DMS001', 1, 3, '2024-06-01', '2025-05-31', 300000.00, 15000.00, 'ACTIVE', 2, 'Расширенная программа ДМС'),
('POL-2024-KASKO001', 2, 2, '2024-03-10', '2025-03-09', 1500000.00, 105000.00, 'ACTIVE', 2, 'КАСКО на Toyota Camry 2023 года'),
('POL-2024-REALTY001', 2, 4, '2024-01-01', '2024-12-31', 5000000.00, 125000.00, 'ACTIVE', 2, 'Страхование квартиры 75 кв.м.'),
('POL-2024-LIFE001', 3, 5, '2024-02-01', '2034-01-31', 2000000.00, 80000.00, 'ACTIVE', 3, 'Накопительное страхование жизни на 10 лет'),
('POL-2023-OSAGO099', 3, 1, '2023-01-01', '2023-12-31', 400000.00, 14000.00, 'EXPIRED', 3, 'Истекший полис ОСАГО');

INSERT INTO insurance_claims (policy_id, claim_number, incident_occurrence_date, incident_description, claimed_compensation_amount, approved_compensation_amount, claim_status, processing_manager_id, resolution_decision_date, additional_notes) VALUES
(1, 'CLM-2024-001', '2024-08-15', 'ДТП на перекрёстке ул. Ленина и пр. Мира. Повреждён передний бампер и правое крыло. Виновник скрылся с места происшествия.', 85000.00, 75000.00, 'APPROVED', 2, '2024-08-20', 'Запрошена экспертиза, одобрено 75 тыс. руб.');

INSERT INTO insurance_claims (policy_id, claim_number, incident_occurrence_date, incident_description, claimed_compensation_amount, approved_compensation_amount, claim_status, processing_manager_id, resolution_decision_date, payment_execution_date, additional_notes) VALUES
(2, 'CLM-2024-002', '2024-09-10', 'Госпитализация в связи с обострением хронического заболевания. Проведена операция по удалению аппендицита.', 120000.00, 120000.00, 'PAID', 2, '2024-09-12', '2024-09-15', 'Выплата произведена в полном объёме');

INSERT INTO insurance_claims (policy_id, claim_number, incident_occurrence_date, incident_description, claimed_compensation_amount, claim_status, processing_manager_id, additional_notes) VALUES
(3, 'CLM-2024-003', '2024-11-25', 'Автомобиль Toyota Camry угнан с парковки у дома ночью 25.11.2024. Заявление в полицию подано, возбуждено уголовное дело.', 1500000.00, 'UNDER_REVIEW', 2, 'Ожидается заключение полиции и экспертная оценка ущерба');

INSERT INTO insurance_claims (policy_id, claim_number, incident_occurrence_date, incident_description, claimed_compensation_amount, approved_compensation_amount, claim_status, processing_manager_id, resolution_decision_date, additional_notes) VALUES
(4, 'CLM-2024-004', '2024-10-05', 'Пожар в квартире из-за короткого замыкания электропроводки. Повреждена мебель и отделка.', 350000.00, 0.00, 'REJECTED', 2, '2024-10-12', 'Отклонено: причина пожара - нарушение правил эксплуатации электроприборов'),
(5, 'CLM-2024-005', '2024-07-20', 'Диагностировано серьёзное заболевание, требующее длительного лечения.', 500000.00, 450000.00, 'APPROVED', 3, '2024-08-01', 'Одобрена выплата по программе страхования жизни');

-- 4. Проверка
SELECT '✅ БАЗА ДАННЫХ УСПЕШНО СОЗДАНА!' AS message;
SELECT '========================================' AS '';
SELECT 'Пользователи для входа:' AS '';
SELECT '----------------------------------------' AS '';
SELECT 
    CONCAT('Email: ', email_address) AS email,
    CONCAT('Пароль: password123') AS password,
    CONCAT('Роль: ', user_role) AS role
FROM system_users;
SELECT '----------------------------------------' AS '';
SELECT 'Всего пользователей: ', COUNT(*) FROM system_users;
SELECT 'Всего клиентов: ', COUNT(*) FROM clients;
SELECT 'Всего полисов: ', COUNT(*) FROM insurance_policies;
SELECT 'Всего страховых случаев: ', COUNT(*) FROM insurance_claims;
