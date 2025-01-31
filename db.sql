create schema Sandc;

create table Student(
	email varchar(50) primary key,
    name varchar(20) not null, 
    address varchar(20) not null,
    phoneNumber varchar(20),
    studyCourse varchar(100),
    cv varchar(1000),
    psw varchar(100) not null,
    token varchar(150)
);

create table Company(
	email varchar(50) primary key,
    name varchar(20) not null,
    address varchar(20) not null,
    phoneNumber varchar(20),
    psw varchar(100) not null,
    token varchar(150)
);

create table Publication (
	id integer primary key auto_increment,
    student varchar(50) not null,
    foreign key (student) references Student(email) on update cascade on delete cascade
);
create table WorkingPreferences( 
	id integer primary key auto_increment,
    text varchar(100) not null
);

create table Preference(
	idWorkingPreferences integer not null, 
    idPublication integer not null, 
    primary key (idWorkingPreferences, idPublication), 
    foreign key (idWorkingPreferences) references WorkingPreferences(id) on update cascade on delete cascade,
    foreign key (idPublication) references Publication(id) on update cascade on delete cascade
);

create table Internship (
	id integer primary key auto_increment, 
    company varchar(50) not null,
    roleToCover varchar(50) not null,
    openSeats int not null,
    startingDate date,
    endingDate date,
    jobDescription varchar(500) not null,
    foreign key (company) references Company(email) on update cascade on delete cascade,	
    check (startingDate < endingDate)
);

create table Matches(
	id integer primary key auto_increment, 
	acceptedYNStudent Boolean,
    acceptedYNCompany Boolean,
    idPublication integer not null,
    idInternship integer not null, 
    foreign key (idPublication) references Publication(id) on update cascade on delete cascade,
    foreign key (idInternship) references Internship(id) on update cascade on delete cascade,
	unique(idPublication, idInternship)
);
create table Requirement(
	idWorkingPreference integer not null, 
    idInternship integer not null, 
    primary key (idWorkingPreference, idInternship), 
    foreign key (idWorkingPreference) references WorkingPreferences(id) on update cascade on delete cascade,
    foreign key (idInternship) references Internship(id) on update cascade on delete cascade
);

create table Form (
	id integer primary key auto_increment 
);
create table Interview (
	id integer primary key auto_increment,
    dat date not null,
    confirmedYN boolean,
	idMatch integer not null,
    idForm integer not null, 
    foreign key (idMatch) references Matches(id) on update cascade on delete cascade,
    foreign key(idForm) references Form(id) on update cascade on delete cascade
);


create table Feedback (
  id integer primary key auto_increment,
  studentYn Boolean not null,
  idForm integer not null,
  studentID varchar(50) not null,
  companyID varchar(50) not null,
  idMatch integer not null,
  foreign key(idForm) references Form(id) on update cascade on delete cascade,
  foreign key(studentID) references Student(email) on update cascade on delete cascade,
  foreign key(companyID) references Company(email) on update cascade on delete cascade,
  foreign key(idMatch) references Matches(id) on update cascade on delete no action
);

create table Complaint (
  id integer primary key auto_increment,
  studentYn Boolean not null,
  idForm integer not null, 
  studentID varchar(50) not null,
  companyID varchar(50) not null,
  idMatch integer not null,
  foreign key(idForm) references Form(id) on update cascade on delete cascade,
  foreign key(studentID) references Student(email) on update cascade on delete cascade,
  foreign key(companyID) references Company(email) on update cascade on delete cascade,
  foreign key(idMatch) references Matches(id) on update cascade on delete no action
);

create table Question(
	id integer primary key auto_increment,
    txt varchar(500) not null, 
    answer varchar(500),
    idForm integer not null, 
    foreign key(idForm) references Form(id) on update cascade on delete cascade
);