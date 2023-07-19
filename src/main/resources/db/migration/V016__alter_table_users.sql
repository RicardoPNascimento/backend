 ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS token varchar(255);

ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS token_creation_date timestamp; 