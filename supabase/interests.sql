-- Enable required extension for UUID generation
create extension if not exists pgcrypto;

-- Create interests table
create table if not exists public.interests (
  id uuid primary key default gen_random_uuid(),
  sender_id uuid not null references auth.users(id) on delete cascade,
  receiver_id uuid not null references auth.users(id) on delete cascade,
  status text not null check (status in ('pending','accepted','rejected')) default 'pending',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- Trigger to keep updated_at fresh
create or replace function public.set_updated_at() returns trigger language plpgsql as $$
begin
  new.updated_at = now();
  return new;
end $$;

drop trigger if exists interests_set_updated_at on public.interests;
create trigger interests_set_updated_at
before update on public.interests
for each row execute procedure public.set_updated_at();

-- Row Level Security
alter table public.interests enable row level security;

-- Insert: only the authenticated sender can create an interest for themselves
create policy "insert own interests"
  on public.interests for insert
  to authenticated
  with check ( sender_id = auth.uid() );

-- Select: show rows where current user is sender or receiver
create policy "select own or received interests"
  on public.interests for select
  to authenticated
  using ( sender_id = auth.uid() or receiver_id = auth.uid() );

-- Update: only the receiver can update (e.g., accept/reject)
create policy "receiver can update status"
  on public.interests for update
  to authenticated
  using ( receiver_id = auth.uid() )
  with check ( receiver_id = auth.uid() );

-- Grants (ensure PostgREST can access with the authenticated role)
grant usage on schema public to authenticated;
grant select, insert, update on table public.interests to authenticated;

-- Ask PostgREST to reload its schema cache (safe no-op if unsupported)
DO $$
begin
  perform pg_notify('pgrst', 'reload schema');
exception when others then
  -- ignore if pg_notify or channel not available
  null;
end $$;