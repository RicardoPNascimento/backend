create table if not exists users_permission
(
    id_user UUID not null,
    id_permission bigint not null,
    primary key (id_user, id_permission),
    foreign key (id_user) references users (id_user),
    foreign key (id_permission) references permission (id_permission)
);