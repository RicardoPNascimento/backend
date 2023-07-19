alter table users
    alter column pix_key set not null;

alter table users
    add unique (pix_key);

alter table users
    alter column phone set not null;

alter table users
    add unique (phone);

