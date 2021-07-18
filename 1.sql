-- Create a new database called 'TutorialDB'
-- Connect to the 'master' database to run this snippet
/*
USE master
GO
IF NOT EXISTS (
   SELECT name
   FROM sys.databases
   WHERE name = N'TutorialDB'
)
CREATE DATABASE TutorialDB
GO

INSERT INTO data VALUES ('Петров Игорь Вячеславович', '1987-11-24', 2536284637);
INSERT INTO data VALUES ('Ефимова Вера Андреевна', '1997-10-12', 2847164728);
INSERT INTO data VALUES ('Макеев Илья Романович', '1967-09-19', 4738591746);
INSERT INTO data VALUES ('Маркелова Милана Николаевна', '1993-01-07', 1859284672);
INSERT INTO data VALUES ('Сурков Даниил Максимович', '1975-07-13', 1958362748);
*/