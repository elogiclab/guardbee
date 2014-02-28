# --- First database schema

# --- !Ups

create table users (
    username varchar(255) not null primary key,
    password varchar(255),
    fullName varchar(255) not null,
    email varchar(255) not null,
    enabled boolean not null,
    expirationDate date
);

create table user_roles (
    username varchar(255) not null,
    role varchar(255) not null
);

ALTER TABLE user_roles ADD PRIMARY KEY (username, role);

create unique index idx_users02 on users(email);
