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
    FoodGroupCode int,
    FoodGroupName varchar(255),
    FoodGroupNameF varchar(255) DEFAULT NULL  -- Nullable - Java ignores this
);

-- Nutrients table (independent entity) - French fields nullable (ignored by Java)
CREATE TABLE nutrients
(
    NutrientID       int PRIMARY KEY,
    NutrientCode     int,
    NutrientSymbol   varchar(20),
    NutrientUnit     varchar(20),
    NutrientName     varchar(255),
    NutrientNameF    varchar(255) DEFAULT NULL,  -- Nullable - Java ignores this
    Tagname          varchar(100),
    NutrientDecimals int
);

-- Foods table (has foreign key to food_groups) - French fields nullable (ignored by Java)
CREATE TABLE foods
(
    FoodID                 int PRIMARY KEY,
    FoodCode               int,
    FoodGroupID            int,
    FoodSourceID           int,
    FoodDescription        varchar(500),
    FoodDescriptionF       varchar(500) DEFAULT NULL,  -- Nullable - Java ignores this
    FoodDateOfEntry        varchar(20) DEFAULT NULL,   -- Nullable - Java ignores this  
    FoodDateOfPublication  varchar(20) DEFAULT NULL,   -- Nullable - Java ignores this
    CountryCode            varchar(10),
    ScientificName         varchar(255),
    
    FOREIGN KEY (FoodGroupID) REFERENCES food_groups(FoodGroupID)
);

-- Nutrient Amounts table (junction table - nutritional values per food)
CREATE TABLE nutrient_amounts
(
    FoodID                int,
    NutrientID           int,
    NutrientValue        double,
    NutrientSourceID     int,
    NutrientDateOfEntry  varchar(20) DEFAULT NULL, -- Nullable - Java ignores this
    
    PRIMARY KEY (FoodID, NutrientID),
    FOREIGN KEY (FoodID) REFERENCES foods(FoodID),
    FOREIGN KEY (NutrientID) REFERENCES nutrients(NutrientID)
);

CREATE TABLE measures
(
    MeasureID           int PRIMARY KEY,
    MeasureDescription  varchar(255),
    MeasureDescriptionF varchar(255) DEFAULT NULL,  -- Nullable - Java ignores this
    CommonName          varchar(255)
);

-- Conversion Factors table (allows converting between different serving sizes)
CREATE TABLE conversion_factors
(
    FoodID                   int,
    MeasureID               int,
    ConversionFactorValue   double,
    ConvFactorDateOfEntry   varchar(20) DEFAULT NULL, -- Nullable - Java ignores this

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
