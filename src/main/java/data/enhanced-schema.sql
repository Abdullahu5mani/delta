-- Enhanced University Management Schema
-- This shows a more complete design with proper relationships

USE delta_database;

-- Students table (enhanced)
CREATE TABLE IF NOT EXISTS students
(
    student_id    int PRIMARY KEY AUTO_INCREMENT,
    student_number varchar(20) UNIQUE NOT NULL,  -- e.g., "123456789"
    first_name    varchar(100) NOT NULL,
    last_name     varchar(100) NOT NULL,
    email         varchar(150) UNIQUE,
    program       varchar(100),           -- e.g., "Computer Science"
    year_level    int,                   -- 1, 2, 3, 4
    enrollment_date date
);

-- Courses table (enhanced)
CREATE TABLE IF NOT EXISTS courses
(
    course_id     varchar(10) PRIMARY KEY,  -- e.g., "EECS3311"
    course_name   varchar(255) NOT NULL,
    credits       int NOT NULL,
    department    varchar(100),
    prerequisites text,                    -- Could be JSON or comma-separated
    description   text
);

-- Professors table (enhanced)  
CREATE TABLE IF NOT EXISTS professors
(
    prof_id       int PRIMARY KEY AUTO_INCREMENT,
    employee_id   varchar(20) UNIQUE,
    first_name    varchar(100) NOT NULL,
    last_name     varchar(100) NOT NULL,
    email         varchar(150) UNIQUE,
    department    varchar(100),
    office_location varchar(50),
    hire_date     date
);

-- Semesters table (new)
CREATE TABLE IF NOT EXISTS semesters
(
    semester_id   int PRIMARY KEY AUTO_INCREMENT,
    semester_code varchar(20) UNIQUE,     -- e.g., "2024W", "2024F"
    semester_name varchar(50),            -- e.g., "Winter 2024"
    start_date    date,
    end_date      date
);

-- Course Offerings (when a course is offered)
CREATE TABLE IF NOT EXISTS course_offerings
(
    offering_id   int PRIMARY KEY AUTO_INCREMENT,
    course_id     varchar(10),
    prof_id       int,
    semester_id   int,
    section       varchar(10),            -- e.g., "A", "B", "LEC01"
    max_enrollment int DEFAULT 100,
    room          varchar(50),
    schedule      varchar(100),           -- e.g., "MWF 10:00-11:00"
    
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (prof_id) REFERENCES professors(prof_id),
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id)
);

-- Student Enrollments
CREATE TABLE IF NOT EXISTS enrollments
(
    enrollment_id int PRIMARY KEY AUTO_INCREMENT,
    student_id    int,
    offering_id   int,
    enrollment_date datetime DEFAULT CURRENT_TIMESTAMP,
    status        enum('enrolled', 'dropped', 'completed') DEFAULT 'enrolled',
    
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (offering_id) REFERENCES course_offerings(offering_id),
    UNIQUE KEY unique_student_offering (student_id, offering_id)
);

-- Grades (enhanced)
CREATE TABLE IF NOT EXISTS grades
(
    grade_id      int PRIMARY KEY AUTO_INCREMENT,
    enrollment_id int,
    letter_grade  varchar(2),             -- A+, A, A-, B+, etc.
    percentage    decimal(5,2),           -- 85.50
    gpa_points    decimal(3,2),           -- 4.00, 3.70, etc.
    grade_date    date,
    
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
);

-- Prerequisites (courses that must be completed before taking another)
CREATE TABLE IF NOT EXISTS course_prerequisites
(
    prerequisite_id int PRIMARY KEY AUTO_INCREMENT,
    course_id       varchar(10),          -- The course that has prerequisites
    prerequisite_course_id varchar(10),   -- The required prerequisite course
    
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (prerequisite_course_id) REFERENCES courses(course_id)
); 