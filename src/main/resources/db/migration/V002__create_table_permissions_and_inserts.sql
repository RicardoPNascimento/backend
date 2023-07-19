create table if not exists permission
(
    id_permission bigint
        primary key,
    role          varchar(255)
);

INSERT INTO permission (id_permission, role)
VALUES ('1','ROLE_ADMIN'),
       ('2','ROLE_USER');