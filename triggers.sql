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
 
create trigger noMindChangesOnAcceptingMatchesStudent
after update on matches
for each row
begin 
	if( old.id = new.id AND exists (SELECT 1 FROM matches WHERE old.acceptedYNStudent is not null AND id = new.id)) then
        set new.acceptedYNStudent = old.acceptedYNStudent;
	end if;
end//

create trigger noMindChangesOnAcceptingMatchesCompany
after update on matches
for each row
begin 
	if( old.id = new.id AND exists (SELECT 1 FROM matches WHERE old.acceptedYNCompany is not null AND id = new.id)) then
		 set new.acceptedYNCompany = old.acceptedYNCompany;
	end if;
end
delimiter ;