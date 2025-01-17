create schema sandc;

create table sandc.Student(
	email varchar(50) primary key,
    name varchar(20) not null, 
    cv varchar(1000) 
);

create table sandc.Company(
	email varchar(50) primary key,
    name varchar(20) not null
);

create table sandc.WorkingPreferences( 
	id integer primary key auto_increment,
    text varchar(100) not null
);

create table sandc.Publication (
	id integer primary key auto_increment,
    student varchar(50) not null,
    foreign key (student) references Student(email) on update cascade on delete cascade
);

create table sandc.Preference(
	idWorkingPreferences integer not null, 
    idPublication integer not null, 
    taken BOOLEAN not null,
    primary key (idWorkingPreferences, idPublication), 
    foreign key (idWorkingPreferences) references WorkingPreferences(id) on update cascade on delete cascade,
    foreign key (idPublication) references Publication(id) on update cascade on delete cascade
);

create table sandc.Internship (
	id integer primary key auto_increment, 
    commonId integer not null,
    company varchar(50) not null,
    startingDate date,
    endingDate date,
    foreign key (company) references Company(email) on update cascade on delete cascade,	
    check (startingDate < endingDate)
);

create table sandc.Requirement(
	idWorkingPreference integer not null, 
    idInternship integer not null, 
    taken BOOLEAN not null,
    primary key (idWorkingPreference, idInternship), 
    foreign key (idWorkingPreference) references WorkingPreferences(id) on update cascade on delete cascade,
    foreign key (idInternship) references Internship(id) on update cascade on delete cascade
);

create table sandc.Matches(
	id integer primary key auto_increment, 
	acceptedYN Boolean,
    idPublication integer not null,
    idInternship integer not null, 
    foreign key (idPublication) references Publication(id) on update cascade on delete cascade,
    foreign key (idInternship) references Internship(id) on update cascade on delete cascade,
	unique(idPublication, idInternship)
);

create table sandc.Form (
	id integer primary key auto_increment 
);
create table sandc.Interview (
	id integer primary key auto_increment,
    dat date not null,
	idMatch integer not null,
    idForm integer not null, 
    foreign key (idMatch) references Matches(id) on update cascade on delete cascade,
    foreign key(idForm) references Form(id) on update cascade on delete cascade
);


create table sandc.Feedback (
	id integer primary key auto_increment,
    studentYn Boolean not null,
	idForm integer not null, 
    foreign key(idForm) references Form(id) on update cascade on delete cascade
);

create table sandc.Complaint (
	id integer primary key auto_increment,
    studentYn Boolean not null,
	idForm integer not null, 
    foreign key(idForm) references Form(id) on update cascade on delete cascade
);

create table sandc.Question(
	id integer primary key auto_increment,
    txt varchar(500) not null, 
    answer varchar(500),
    idForm integer not null, 
    foreign key(idForm) references Form(id) on update cascade on delete cascade
);

