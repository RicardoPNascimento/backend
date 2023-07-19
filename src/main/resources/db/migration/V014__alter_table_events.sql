ALTER TABLE public.events
    ADD COLUMN IF NOT EXISTS card_title varchar(256);