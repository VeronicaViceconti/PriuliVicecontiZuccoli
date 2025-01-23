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

delimiter //  
create trigger noMindChangesOnAcceptingMatchesStudent
before update on matches
for each row
begin 
	if( old.id = new.id AND exists (SELECT 1 FROM matches WHERE old.acceptedYNStudent is not null AND id = new.id)) then
        set new.acceptedYNStudent = old.acceptedYNStudent;
	end if;
end//

create trigger noMindChangesOnAcceptingMatchesCompany
before update on matches
for each row
begin 
	if( old.id = new.id AND exists (SELECT 1 FROM matches WHERE old.acceptedYNCompany is not null AND id = new.id)) then
		 set new.acceptedYNCompany = old.acceptedYNCompany;
	end if;
ends
 
delimiter ;
 
 delimiter //
 create trigger deleteMatchWhenNoMoreAvailableSeats 
 after update on interview
 for each row 
 begin 
	declare intId integer;
    declare seats integer;
	declare occupied integer;
    
    select m.idInternship into intId
    from interview as i inner join matches as m on i.idMatch = m.id
    where i.id = new.id;
	
	select openSeats into seats from internship where id = intId;
    
    select count(*) into occupied
    from matches as m inner join interview as i on i.idMatch = m.id
    where confirmedYN = true and m.idInternship = intId;
 
	if new.confirmedYN <> old.confirmedYN then
		if seats = occupied then
			create temporary table temp_match as 
            select m.id
			from matches as m inner join interview as i on  i.idMatch = m.id
			where i.confirmedYN = true and m.idInternship = intId;
            
			delete from matches where id not in (select id from temp_match) and idInternship = intId;
            
            drop temporary table temp_match;
        end if;
	end if;
 end//
  
  
 create trigger deleteMatchFalseInterview
 after update on interview
 for each row
 begin
	if( old.confirmedYN is null and new.confirmedYN is false and old.idMatch = new.idMatch) then
		DELETE from matches where id = new.idMatch;
        DELETE from form where id = new.idForm;
    end if;
 end//
 
 delimiter //
 create trigger deletePubYesInterview
 after update on interview
 for each row
 begin
	declare stud varchar(50);
    declare idPub integer;
    SELECT p.id into idPub from interview as i join matches as m on i.idMatch = m.id join publication as p on p.id = m.idPublication WHERE i.idMatch = new.idMatch;
    
    SELECT s.email into stud FROM interview as i join matches as m on i.idMatch = m.id join publication as p on p.id = m.idPublication join student as s on s.email = p.student WHERE i.idMatch = new.idMatch;  
	if( old.confirmedYN is null and new.confirmedYN is true and old.idMatch = new.idMatch) then
		DELETE from publication where student = stud and id  <> idPub;
    end if;
 end//
 
create trigger noMindChangesOnAcceptingInterviewCompany
before update on interview
for each row
begin 
	if( old.id = new.id AND exists (SELECT 1 FROM interview WHERE old.confirmedYN is not null AND id = new.id)) then
		 set new.confirmedYN = old.confirmedYN;
	end if;
end//

 delimiter ;