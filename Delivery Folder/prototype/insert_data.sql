INSERT INTO WorkingPreferences (`id`, `text`) VALUES
(1, 'Artificial Intelligence'),
(2, 'Robotics'),
(3, 'Database Management'),
(4, 'Embedded Systems'),
(5, 'Web Development'),
(6, 'Cybersecurity'),
(7, 'Machine Learning'),
(8, 'Data Science'),
(9, 'Network Security'),
(10, 'Mobile App Development'),
(11, 'Cloud Computing'),
(12, 'Software Engineering');


INSERT INTO Student (email, name, address, phoneNumber, studyCourse, psw) VALUES
('alice.johnson@example.com', 'Alice Johnson', '123 Maple Street', '555-0100', 'Computer Science','alicePass'),
('bob.smith@example.com', 'Bob Smith', '456 Oak Avenue', '555-0200', 'Software Engineering', 'bobPass'),
('carol.martinez@example.com', 'Carol Martinez', '789 Pine Road', '555-0300', 'Information Systems', 'carolPass'),
('david.lee@example.com', 'David Lee', '321 Birch Lane', '555-0400', 'Electrical Engineering',  'davidPass'),
('emma.brown@example.com', 'Emma Brown', '654 Cedar Boulevard', '555-0500', 'Cybersecurity',  'emmaPass');


INSERT INTO Company (email, name, address, phoneNumber, psw) VALUES
('contact@techcorp.com', 'TechCorp', '100 Tech Way', '555-1100', 'techPass'),
('hr@innovateinc.com', 'Innovate Inc', '200 Innovation Drive', '555-1200', 'innovatePass'),
('info@futureware.com', 'Futureware', '300 Future Road', '555-1300', 'futurePass'),
('support@netsolutions.com', 'NetSolutions', '400 Network Street', '555-1400', 'netPass'),
('jobs@aidynamics.com', 'AI Dynamics', '500 AI Avenue', '555-1500', 'aiPass');

INSERT INTO Publication (student) VALUES
('alice.johnson@example.com'),  -- Publication ID 1
('bob.smith@example.com'),      -- Publication ID 2
('carol.martinez@example.com'), -- Publication ID 3
('david.lee@example.com'),      -- Publication ID 4
('emma.brown@example.com');     -- Publication ID 5

-- Alice Johnson prefers Artificial Intelligence and Machine Learning
INSERT INTO Preference (idWorkingPreferences, idPublication) VALUES
(1, 1),
(7, 1);

-- Bob Smith prefers Web Development and Software Engineering
INSERT INTO Preference (idWorkingPreferences, idPublication) VALUES
(5, 2),
(12, 2);

-- Carol Martinez prefers Data Science and Database Management
INSERT INTO Preference (idWorkingPreferences, idPublication) VALUES
(8, 3),
(3, 3);

-- David Lee prefers Robotics and Embedded Systems
INSERT INTO Preference (idWorkingPreferences, idPublication) VALUES
(2, 4),
(4, 4);

-- Emma Brown prefers Cybersecurity and Network Security
INSERT INTO Preference (idWorkingPreferences, idPublication) VALUES
(6, 5),
(9, 5);

INSERT INTO Internship (company, roleToCover, openSeats, startingDate, endingDate, jobDescription) VALUES
('contact@techcorp.com', 'AI Research Intern', 4, '2024-06-01', '2025-12-31', 'Work on cutting-edge AI projects.'), -- on going
('hr@innovateinc.com', 'Web Developer Intern', 3, '2025-07-01', '2025-10-31', 'Develop and maintain web applications.'),
('info@futureware.com', 'Data Analyst Intern', 1, '2022-01-01', '2022-06-30', 'Analyze datasets for valuable insights.'),  -- Internship ended
('support@netsolutions.com', 'Network Security Intern', 1, '2023-01-01', '2023-06-30', 'Secure and monitor network infrastructure.'),  -- Internship ended
('jobs@aidynamics.com', 'Robotics Intern', 1, '2024-09-01', '2025-03-31', 'Develop and test robotic systems.'),
('contact@techcorp.com', 'AI Research Intern', 2, '2025-06-01', '2025-12-31', 'Assist in research on AI algorithms and models.');


-- Alice Johnson's matches
INSERT INTO Matches (acceptedYNStudent, acceptedYNCompany, idPublication, idInternship) VALUES
(NULL, NULL, 1, 1),  -- Match ID 1
(NULL, NULL, 1, 3);  -- Match ID 2

-- Bob Smith's matches
INSERT INTO Matches (acceptedYNStudent, acceptedYNCompany, idPublication, idInternship) VALUES
(NULL, NULL, 2, 2),  -- Match ID 3
(NULL, NULL, 2, 1);  -- Match ID 4

-- Carol Martinez's matches
INSERT INTO Matches (acceptedYNStudent, acceptedYNCompany, idPublication, idInternship) VALUES
(NULL, NULL, 3, 3),  -- Match ID 5
(NULL, NULL, 3, 4);  -- Match ID 6

-- David Lee's matches
INSERT INTO Matches (acceptedYNStudent, acceptedYNCompany, idPublication, idInternship) VALUES
(NULL, NULL, 4, 5),  -- Match ID 7
(NULL, NULL, 4, 1);  -- Match ID 8

-- Emma Brown's matches
INSERT INTO Matches (acceptedYNStudent, acceptedYNCompany, idPublication, idInternship) VALUES
(NULL, NULL, 5, 4),  -- Match ID 9
(NULL, NULL, 5, 2);  -- Match ID 10

-- TechCorp requires Artificial Intelligence and Machine Learning
INSERT INTO Requirement (idWorkingPreference, idInternship) VALUES
(1, 1),
(7, 1),
(1, 6),
(7, 6);

-- Innovate Inc requires Web Development and Software Engineering
INSERT INTO Requirement (idWorkingPreference, idInternship) VALUES
(5, 2),
(12, 2);

-- Futureware requires Data Science and Database Management
INSERT INTO Requirement (idWorkingPreference, idInternship) VALUES
(8, 3),
(3, 3);

-- NetSolutions requires Cybersecurity and Network Security
INSERT INTO Requirement (idWorkingPreference, idInternship) VALUES
(6, 4),
(9, 4);

-- AI Dynamics requires Robotics and Embedded Systems
INSERT INTO Requirement (idWorkingPreference, idInternship) VALUES
(2, 5),
(4, 5);

-- Alice Johnson accepts her matches
UPDATE Matches SET acceptedYNStudent = TRUE WHERE id IN (1, 2);

-- Bob Smith accepts his matches
UPDATE Matches SET acceptedYNStudent = TRUE WHERE id IN (3, 4);

-- Carol Martinez accepts her matches
UPDATE Matches SET acceptedYNStudent = TRUE WHERE id IN (5, 6);

-- David Lee accepts his matches
UPDATE Matches SET acceptedYNStudent = TRUE WHERE id IN (7, 8);

-- Emma Brown accepts her matches
UPDATE Matches SET acceptedYNStudent = TRUE WHERE id IN (9, 10);

-- TechCorp accepts matches with Alice and Bob
UPDATE Matches SET acceptedYNCompany = TRUE WHERE id IN (1, 4, 8);

-- Futureware accepts matches with Alice and Carol
UPDATE Matches SET acceptedYNCompany = TRUE WHERE id IN (2, 5);

-- Innovate Inc accepts matches with Bob and Emma
UPDATE Matches SET acceptedYNCompany = TRUE WHERE id IN (3, 10);

-- NetSolutions accepts match with Emma
UPDATE Matches SET acceptedYNCompany = TRUE WHERE id = 9;

-- AI Dynamics has not confirmed matches yet

-- Create forms for interviews
INSERT INTO Form (id) VALUES (1), (2), (3), (4), (5), (6);


-- Alice Johnson's confirmed match with TechCorp (Match ID 1)
INSERT INTO Interview (dat, confirmedYN, idMatch, idForm) VALUES
('2023-06-15', TRUE, 1, 1);  -- Interview confirmed

-- Alice's match with Futureware (Match ID 2) had an interview in the past
INSERT INTO Interview (dat, confirmedYN, idMatch, idForm) VALUES
('2021-05-10', TRUE, 2, 2);  -- Past interview, internship ended

-- Bob Smith's confirmed match with Innovate Inc (Match ID 3)
INSERT INTO Interview (dat, confirmedYN, idMatch, idForm) VALUES
('2025-01-11', null, 3, 3);  -- Interview scheduled, confirmation pending

-- Carol Martinez's confirmed match with Futureware (Match ID 5)
INSERT INTO Interview (dat, confirmedYN, idMatch, idForm) VALUES
('2022-02-01', TRUE, 5, 4);  -- Past interview, internship ended

-- David Lee's confirmed match with TechCorp (Match ID 8)
INSERT INTO Interview (dat, confirmedYN, idMatch, idForm) VALUES
('2023-09-10', TRUE, 8, 5);  -- Interview confirmed

-- Emma Brown's confirmed match with NetSolutions (Match ID 9)
INSERT INTO Interview (dat, confirmedYN, idMatch, idForm) VALUES
('2022-09-15', TRUE, 9, 6);  -- Interview confirmed


insert into question values (1, 'question 1', 'answer 1', 3), (2, 'question 2', 'answer 2', 3),(3, 'question 3', 'answer 3', 3);