use Crux_Conqueror;
create table users( 
User_ID INT auto_increment primary key,
Usernames Varchar(25) not null unique,
Emails Varchar(50) not null unique,
Password_Hash Varchar(250) not null,
Date_Made datetime default CURRENT_TIMESTAMP() 
);