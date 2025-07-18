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
 
  delimiter //
create trigger matchMakerStudents
 after insert on Preference 
 for each row
 begin 
    declare num Integer;
    select count(*) into num
    from preference
    where idPublication = new.idPublication;

    insert into Matches (idPublication, idInternship)
        select idPublication, idInternship as internship
        from  Preference as p inner join Requirement as r on p.idWorkingPreferences = r.idWorkingPreference
        where idPublication = new.idPublication and not exists (select * from Matches as m where m.idPublication = p.idPublication and r.idInternship = m.idInternship) 
            and (select openSeats from internship where internship.id = r.idInternship) > 0 and current_date() < (select startingDate from internship where internship.id = r.idInternship)
        group by idPublication, idInternship
        having count(*) >= (num - 1) or count(*) >= ((select count(*) from requirement where idInternship = r.idInternship) -1) ;
 end //
 

 create trigger matchMakerCompanies
 after insert on Requirement 
 for each row 
 begin 
	declare num Integer;
    select count(*) into num
    from requirement
	where idInternship = new.idInternship; 

	insert into Matches (idPublication, idInternship)  
		select idPublication, idInternship 
		from  Preference as p inner join Requirement as r on p.idWorkingPreferences = r.idWorkingPreference
        where idInternship = new.idInternship and not exists (select * from Matches as m where m.idPublication = p.idPublication and r.idInternship = m.idInternship) 
			and not exists (select * from matches as m1 inner join interview as i on m1.id = i.idMatch where i.confirmedYN = 1 and m1.idPublication = p.idPublication)
        group by idPublication, idInternship
        having count(*) >= (num - 1) or count(*) >= ((select count(*) from preference where idPublication = p.idPublication) -1) ;
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
end//
 
delimiter ;
 
 delimiter //
 create trigger updateOpenSeats
 after update on interview
 for each row
 begin
	declare internToUpdate int;
    select inter.id into internToUpdate from interview as i join matches as m on i.idMatch = m.id join internship as inter on inter.id = m.idInternship where m.id = new.idMatch;
	if new.confirmedYN is true then
		update internship set openSeats = openSeats - 1 where id = internToUpdate;
	end if;
 end //
 
 
delimiter //
 create trigger deleteMatchWhenNoMoreAvailableSeats 
 after update on internship
 for each row 
 begin 
    if new.openSeats = 0 then
        create temporary table temp_match as 
        select m.id
        from matches as m inner join interview as i on  i.idMatch = m.id
        where i.confirmedYN = true and m.idInternship = new.id;

        delete from matches where id not in (select id from temp_match) and idInternship = new.id;

        drop temporary table temp_match;
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
 
 create trigger oneOngoingForStudent
 before update on interview
 for each row
 begin
	DECLARE emailStudent varchar(50);
    SELECT email into emailStudent FROM student as s join publication as p on s.email = p.student join matches as m on m.idPublication = p.id where m.id = new.idMatch;
	if( new.confirmedYN is true and (SELECT count(*) FROM matches as m JOIN internship as i on i.id = m.idInternship JOIN publication as p on  p.id = m.idPublication right join student as s on s.email = p.student join interview on interview.idMatch = m.id WHERE s.email = emailStudent and interview.confirmedYN = 1 and i.startingDate <= current_date() and i.endingDate >= current_date()) = 1 ) then
        DELETE from matches where id = new.idMatch;
        DELETE from form where id = new.idForm;
	end if;
 end//
 
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