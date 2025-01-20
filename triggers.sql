delimiter //
create trigger noStudentAndCompany
before insert on Student
for each row
begin
IF EXISTS (SELECT * FROM Company WHERE email = NEW.email) 
	THEN 
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'the email is already a company'; 
    END IF;
 end//

create trigger noCompanyAndStudent
before insert on Company
for each row
begin
IF EXISTS (SELECT * FROM Student WHERE email = NEW.email) 
	THEN 
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'the mail is already a student'; 
    END IF;
 end//
 
 create trigger maxOneMatch
 before insert on Matches
 for each row
 begin
	declare s varchar(50);
    declare c varchar(50); 
    select student into s from Publication where id = new.idPublication;
    select company into c from Internship where id = new.idInternship;
    
	if exists (select * from Publication inner join Matches inner join Internship where student = s and company = c) then
		set new.idPublication = null;
        set new.idInternship = null;
	end if;
 end//
 
 create trigger matchMakerStudents
 after insert on Preference 
 for each row 
 begin 
	insert into Matches (idPublication, idInternship)  
		select idPublication, idInternship 
		from  Preference as p inner join Requirement as r on p.idWorkingPreferences = r.idWorkingPreference
        where idPublication = new.idPublication and not exists (select * from Matches as m where m.idPublication = p.idPublication and r.idInternship)
        group by idPublication, idInternship
        having count(*) >= 5;
 end //
 
 create trigger matchMakerCompanies
 after insert on Requirement 
 for each row 
 begin 
	insert into Matches (idPublication, idInternship)  
		select idPublication, idInternship 
		from  Preference as p inner join Requirement as r on p.idWorkingPreferences = r.idWorkingPreference
        where idInternship = new.idInternship and not exists (select * from Matches as m where m.idPublication = p.idPublication and r.idInternship)
        group by idPublication, idInternship
        having count(*) >= 5;
 end //
 
 
 delimiter ;