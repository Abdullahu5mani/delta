DROP DATABASE IF EXISTS delta_database;
CREATE DATABASE delta_database;

USE delta_database;

-- Example table for testing
CREATE TABLE students
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255)
);

-- Represents users of the application
CREATE TABLE profiles
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    full_name   VARCHAR(80) NOT NULL,
    age         INT         NOT NULL,
    sex         VARCHAR(10) NOT NULL,
    dob         DATE        NOT NULL,
    height      DOUBLE      NOT NULL,
    weight      DOUBLE      NOT NULL,
    unit_system VARCHAR(10) NOT NULL
);

-- A meal containing multiple items
CREATE TABLE meals
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    meal_type  VARCHAR(10),
    user_id    INT  NOT NULL,
    created_on DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES profiles (id)
);

-- One item within a meal
CREATE TABLE meal_items
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    meal_id    INT,
    food_id    INT   NOT NULL,
    quantity   FLOAT NOT NULL,
    -- TO DO: disabling NOT NULL check until the create meal service is updated
    -- to include the measure ID when saving a record to the database
    measure_id INT,
    FOREIGN KEY (meal_id) REFERENCES meals (id)
    -- TO DO: disabling foreign key reference temporarily while using mock food data
    -- until we finalize foods in database and move off of using mocks entirely
    -- FOREIGN KEY (food_id) REFERENCES foods(food_id)
);

-- ======================================
-- NUTRITION TABLES
-- ======================================

-- Food Groups table (independent entity)
CREATE TABLE food_groups
(
    id   INT PRIMARY KEY,
    name VARCHAR(255)
);

-- Nutrients table (independent entity)
CREATE TABLE nutrients
(
    id     INT PRIMARY KEY,
    symbol VARCHAR(20),
    unit   VARCHAR(20),
    name   VARCHAR(255)
);

-- Foods table (has foreign key to food_groups)
CREATE TABLE foods
(
    id            INT PRIMARY KEY,
    food_group_id INT,
    description   VARCHAR(500),
    FOREIGN KEY (food_group_id) REFERENCES food_groups (id)
);

-- Nutrient Amounts table (junction table - nutritional values per food)
CREATE TABLE nutrient_amounts
(
    food_id        INT,
    nutrient_id    INT,
    nutrient_value DOUBLE,

    PRIMARY KEY (food_id, nutrient_id),
    FOREIGN KEY (food_id) REFERENCES foods (id),
    FOREIGN KEY (nutrient_id) REFERENCES nutrients (id)
);

-- Food measurement (ex. "1 Cup")
CREATE TABLE measures
(
    id          INT PRIMARY KEY,
    common_name VARCHAR(255)
);

-- Conversion Factors table (allows converting between different serving sizes)
CREATE TABLE conversion_factors
(
    food_id                 INT,
    measure_id              INT,
    conversion_factor_value DOUBLE,

    PRIMARY KEY (food_id, measure_id),
    FOREIGN KEY (food_id) REFERENCES foods (id),
    FOREIGN KEY (measure_id) REFERENCES measures (id)
);

-- View to get conversion factors and measure name in single query
CREATE VIEW conversion_factors_with_measure_details AS
SELECT cf.food_id AS food_id,
       cf.measure_id AS measure_id,
       m.common_name AS measure_name,
       cf.conversion_factor_value AS conversion_factor_value
FROM conversion_factors cf
JOIN measures m ON cf.measure_id = m.id
