create table if not exists users
(
    id_user                 uuid primary key unique,
    full_name               varchar(250) not null,
    cpf                     varchar(20)  not null unique,
    email                   varchar(250) not null unique ,
    birth_date              date,
    ZIP                     varchar(20) not null,
    street                  varchar(250),
    district                varchar(250),
    city                    varchar(20),
    state                   varchar(20),
    country                 varchar(20),
    number                  varchar(20),
    has_children            boolean,
    nearby_airport          varchar(250),
    children_qty            int,
    children_names          varchar(256),
    password                varchar(256) not null,
    account_non_expired     boolean,
    account_non_locked      boolean,
    credentials_non_expired boolean,
    enabled                 boolean,
    created_at              timestamp default current_timestamp,
    marital_state           varchar(20)
);

