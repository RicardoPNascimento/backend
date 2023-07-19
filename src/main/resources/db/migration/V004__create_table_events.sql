create table events
(
    event_id     SERIAL primary key,
    id_user      uuid,
    type_day     varchar(255),
    date         date,
    start_time   time,
    end_time     time,
    document     bytea,
    "created_at" timestamp   default current_timestamp,
    status       varchar(20) default 'Pendente'
);


