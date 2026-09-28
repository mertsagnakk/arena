-- Supabase SQL Editor'de çalıştırın.
-- Mevcut 'duels' tablosuna arkadaşla oda kurma desteği ekler.

alter table duels
  add column if not exists room_code text,
  add column if not exists mode text not null default 'random'; -- 'random' | 'private'

-- Aynı kod iki farklı bekleyen odada kullanılmasın diye:
create unique index if not exists idx_duels_room_code_waiting
  on duels (room_code)
  where status = 'waiting' and room_code is not null;
