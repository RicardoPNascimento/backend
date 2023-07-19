ALTER TABLE public.events
    ADD COLUMN IF NOT EXISTS reason varchar(256);

ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS user_name varchar(255);