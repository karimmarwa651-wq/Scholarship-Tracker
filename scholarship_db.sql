DROP DATABASE IF EXISTS scholarship_db;
CREATE DATABASE scholarship_db;
USE scholarship_db;

CREATE TABLE Sponsors (
    SponsorID   INT PRIMARY KEY AUTO_INCREMENT,
    Name        VARCHAR(100) NOT NULL,
    Budget      DECIMAL(12,2) NOT NULL
);

CREATE TABLE Students (
    StudentID    INT PRIMARY KEY AUTO_INCREMENT,
    Name         VARCHAR(100) NOT NULL,
    Department   VARCHAR(100) NOT NULL,
    CGPA         DECIMAL(3,2) NOT NULL,
    FamilyIncome DECIMAL(12,2) NOT NULL
);

CREATE TABLE Scholarships (
    ScholarshipID   INT PRIMARY KEY AUTO_INCREMENT,
    Name            VARCHAR(100) NOT NULL,
    MinCGPA         DECIMAL(3,2) NOT NULL,
    MaxIncome       DECIMAL(12,2) NOT NULL,
    Amount          DECIMAL(12,2) NOT NULL,
    SponsorID       INT NOT NULL,
    ScholarshipType VARCHAR(20),
    FOREIGN KEY (SponsorID) REFERENCES Sponsors(SponsorID)
);

CREATE TABLE ScholarshipRecords (
    AppID         INT PRIMARY KEY AUTO_INCREMENT,
    StudentID     INT NOT NULL,
    ScholarshipID INT NOT NULL,
    ApplyDate     DATE NOT NULL,
    Status        ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    FOREIGN KEY (StudentID)     REFERENCES Students(StudentID),
    FOREIGN KEY (ScholarshipID) REFERENCES Scholarships(ScholarshipID)
);

INSERT INTO Sponsors (Name, Budget) VALUES
('OGDCL CS Department',       2000000.00),
('HEC Need-Based Fund',        5000000.00),
('Sindh Education Foundation', 3000000.00),
('Ehsaas Program NGO',         1500000.00);

INSERT INTO Students (Name, Department, CGPA, FamilyIncome) VALUES
('Ali Hassan',     'Computer Science',     3.85, 35000.00),
('Sara Khan',      'Software Engineering', 3.60, 42000.00),
('Usman Tariq',    'Information Tech',     2.90, 55000.00),
('Marwa Karim',    'Computer Science',     3.75, 28000.00),
('Fatima Noor',    'Electrical Eng',       3.40, 48000.00),
('Bilal Ahmed',    'Computer Science',     3.10, 60000.00),
('Zainab Raza',    'Software Engineering', 3.90, 22000.00),
('Hamza Siddiqui', 'Information Tech',     2.75, 70000.00),
('Ayesha Malik',   'Electrical Eng',       3.55, 38000.00),
('Talha Javed',    'Computer Science',     3.20, 51000.00);

INSERT INTO Scholarships (Name, MinCGPA, MaxIncome, Amount, SponsorID, ScholarshipType) VALUES
('OGDCL Merit Scholarship',        3.70, 40000.00, 80000.00, 1, 'MERIT_BASED'),
('HEC Need-Based Scholarship',     3.00, 50000.00, 60000.00, 2, 'NEED_BASED'),
('SEF Academic Excellence Award',  3.50, 45000.00, 50000.00, 3, 'MERIT_BASED'),
('Ehsaas Education Support Grant', 2.50, 65000.00, 40000.00, 4, 'BOTH');

INSERT INTO ScholarshipRecords (StudentID, ScholarshipID, ApplyDate, Status) VALUES
(1, 1, '2025-01-10', 'Pending'),
(2, 3, '2025-01-12', 'Pending'),
(3, 4, '2025-01-15', 'Pending'),
(4, 1, '2025-01-18', 'Pending'),
(5, 2, '2025-01-20', 'Pending'),
(6, 2, '2025-01-22', 'Pending'),
(7, 1, '2025-02-01', 'Pending'),
(8, 4, '2025-02-03', 'Pending'),
(9, 3, '2025-02-05', 'Pending'),
(10, 4, '2025-02-08', 'Pending');

-- eligibility check and update status
UPDATE ScholarshipRecords SR
JOIN Students ST ON SR.StudentID = ST.StudentID
JOIN Scholarships SC ON SR.ScholarshipID = SC.ScholarshipID
SET SR.Status = 'Approved'
WHERE SC.ScholarshipType = 'NEED_BASED'
  AND ST.CGPA >= 2.0
  AND ST.FamilyIncome <= 50000;

UPDATE ScholarshipRecords SR
JOIN Students ST ON SR.StudentID = ST.StudentID
JOIN Scholarships SC ON SR.ScholarshipID = SC.ScholarshipID
SET SR.Status = 'Approved'
WHERE SC.ScholarshipType = 'MERIT_BASED'
  AND ST.CGPA >= 3.5
  AND ST.FamilyIncome <= 100000;

UPDATE ScholarshipRecords SR
JOIN Students ST ON SR.StudentID = ST.StudentID
JOIN Scholarships SC ON SR.ScholarshipID = SC.ScholarshipID
SET SR.Status = 'Approved'
WHERE SC.ScholarshipType = 'BOTH'
  AND ST.CGPA >= 2.8
  AND ST.FamilyIncome <= 80000;

UPDATE ScholarshipRecords
SET Status = 'Rejected'
WHERE Status = 'Pending';

SELECT
    SR.AppID,
    ST.Name        AS StudentName,
    ST.Department,
    ST.CGPA,
    ST.FamilyIncome,
    SC.Name        AS ScholarshipName,
    SC.ScholarshipType,
    SP.Name        AS SponsorName,
    SR.ApplyDate,
    SR.Status
FROM ScholarshipRecords SR
JOIN Students     ST ON SR.StudentID     = ST.StudentID
JOIN Scholarships SC ON SR.ScholarshipID = SC.ScholarshipID
JOIN Sponsors     SP ON SC.SponsorID     = SP.SponsorID
ORDER BY SR.AppID;

SELECT Status, COUNT(*) AS Total
FROM ScholarshipRecords
GROUP BY Status;