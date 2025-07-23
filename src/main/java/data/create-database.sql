DROP DATABASE IF EXISTS delta_database;
CREATE DATABASE delta_database;

USE delta_database;

-- example table for testing
CREATE TABLE students
(
    id   int PRIMARY KEY AUTO_INCREMENT,
    name varchar(255)
);

CREATE TABLE profiles(
     id            INT PRIMARY KEY AUTO_INCREMENT,
     full_name     VARCHAR(80) NOT NULL,
     age           INT         NOT NULL,
     sex           VARCHAR(10) NOT NULL,
     dob           DATE        NOT NULL,
     height        DOUBLE      NOT NULL,
     weight        DOUBLE      NOT NULL,
     unit_system   VARCHAR(10) NOT NULL
);

-- ======================================
-- NUTRITION TABLES
-- ======================================

-- Food Groups table (independent entity) - French fields nullable (ignored by Java)
CREATE TABLE food_groups
(
    FoodGroupID   int PRIMARY KEY,
    FoodGroupName varchar(255)
);

-- Nutrients table (independent entity)
CREATE TABLE nutrients
(
    NutrientID       int PRIMARY KEY,
    NutrientSymbol   varchar(20),
    NutrientUnit     varchar(20),
    NutrientName     varchar(255)
);

-- Foods table (has foreign key to food_groups) - French fields nullable (ignored by Java)
CREATE TABLE foods
(
    FoodID                 int PRIMARY KEY,
    FoodGroupID            int,
    FoodDescription        varchar(500),
    FOREIGN KEY (FoodGroupID) REFERENCES food_groups(FoodGroupID)
);

-- Nutrient Amounts table (junction table - nutritional values per food)
CREATE TABLE nutrient_amounts
(
    FoodID               int,
    NutrientID           int,
    NutrientValue        double,

    PRIMARY KEY (FoodID, NutrientID),
    FOREIGN KEY (FoodID) REFERENCES foods(FoodID),
    FOREIGN KEY (NutrientID) REFERENCES nutrients(NutrientID)
);

CREATE TABLE measures
(
    MeasureID           int PRIMARY KEY,
    MeasureDescription  varchar(255)
);

-- Conversion Factors table (allows converting between different serving sizes)
CREATE TABLE conversion_factors
(
    FoodID                  int,
    MeasureID               int,
    ConversionFactorValue   double,

    PRIMARY KEY (FoodID, MeasureID),
    FOREIGN KEY (FoodID) REFERENCES foods(FoodID),
    FOREIGN KEY (MeasureID) REFERENCES measures(MeasureID)
);

-- Meal tables

CREATE TABLE meals
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    meal_type VARCHAR(10),
    user_id INT NOT NULL ,
    created_on DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES profiles(id)
);

CREATE TABLE meal_items
(
    id int PRIMARY KEY AUTO_INCREMENT,
    meal_id INT,
    food_id INT NOT NULL,
    quantity FLOAT NOT NULL,
    FOREIGN KEY (meal_id) REFERENCES meals(id)
    -- TO DO: disabling foreign key reference temporarily while using mock food data
    -- until we finalize foods in database and move off of using mocks entirely
    -- FOREIGN KEY (food_id) REFERENCES foods(FoodID)
);
