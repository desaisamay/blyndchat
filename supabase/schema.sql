-- Supabase schema for profiles and basic RLS policy
create table if not exists public.profiles (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  age int,
  height text,
  religion text,
  caste text,
  profession text,
  location text,
  image_url text,
  about text,
  gender text,
  annual_income text,
  phone_number text,
  created_at timestamptz default now()
);

alter table public.profiles enable row level security;

-- Allow anonymous read for all rows (adjust as needed)
create policy "Public read profiles"
  on public.profiles
  for select
  to anon
  using (true);

-- Allow authenticated users to insert their own profile (id must equal auth.uid())
drop policy if exists "Insert own profile" on public.profiles;
create policy "Insert own profile"
  on public.profiles
  for insert
  to authenticated
  with check (id = auth.uid());

-- Allow authenticated users to update their own profile
drop policy if exists "Update own profile" on public.profiles;
create policy "Update own profile"
  on public.profiles
  for update
  to authenticated
  using (id = auth.uid())
  with check (id = auth.uid());

-- Optional: bucket for profile photos (create in Studio UI)
-- Use a public bucket named 'profiles' and store image public URLs in image_url column.

-- Auto-create a profile row when a new Supabase Auth user is created
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into public.profiles (
    id, name, age, height, religion, caste, profession, location,
    image_url, about, gender, annual_income, phone_number
  )
  values (
    new.id,
    coalesce(
      nullif(trim(new.raw_user_meta_data->>'name'), ''),
      nullif(trim(new.raw_user_meta_data->>'full_name'), ''),
      nullif(trim(split_part(new.email, '@', 1)), ''),
      nullif(trim(new.phone), ''),
      'User'
    ),
    nullif((new.raw_user_meta_data->>'age')::int, 0),
    nullif(trim(new.raw_user_meta_data->>'height'), ''),
    nullif(trim(new.raw_user_meta_data->>'religion'), ''),
    nullif(trim(new.raw_user_meta_data->>'caste'), ''),
    nullif(trim(new.raw_user_meta_data->>'profession'), ''),
    nullif(trim(new.raw_user_meta_data->>'location'), ''),
    nullif(trim(new.raw_user_meta_data->>'image_url'), ''),
    nullif(trim(new.raw_user_meta_data->>'about'), ''),
    nullif(trim(new.raw_user_meta_data->>'gender'), ''),
    nullif(trim(new.raw_user_meta_data->>'annual_income'), ''),
    nullif(trim(new.raw_user_meta_data->>'phone_number'), '')
  );
  return new;
end;
$$;

-- Recreate trigger to ensure it exists and points to the latest function body
drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();

-- Interests: a user sends an interest to another user. Accepting creates a chat.
create table if not exists public.interests (
  id uuid primary key default gen_random_uuid(),
  sender_id uuid not null,
  receiver_id uuid not null,
  status text not null default 'pending', -- pending | accepted | rejected
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

alter table public.interests enable row level security;

-- RLS: sender can insert interests they create; both sender and receiver can read
drop policy if exists "Insert own interest" on public.interests;
create policy "Insert own interest"
  on public.interests
  for insert
  to authenticated
  with check (sender_id = auth.uid());

drop policy if exists "Read my interests" on public.interests;
create policy "Read my interests"
  on public.interests
  for select
  to authenticated
  using (sender_id = auth.uid() or receiver_id = auth.uid());

-- Update status: only receiver can accept/reject their received interests
drop policy if exists "Receiver updates status" on public.interests;
create policy "Receiver updates status"
  on public.interests
  for update
  to authenticated
  using (receiver_id = auth.uid())
  with check (receiver_id = auth.uid());

-- Messages: chat messages between two users, grouped by conversation_id
create table if not exists public.messages (
  id uuid primary key default gen_random_uuid(),
  conversation_id uuid not null,
  sender_id uuid not null,
  receiver_id uuid not null,
  content text not null,
  created_at timestamptz default now()
);

alter table public.messages enable row level security;

-- Read/write messages only if sender or receiver is the authed user
drop policy if exists "Select own conversations" on public.messages;
create policy "Select own conversations"
  on public.messages
  for select
  to authenticated
  using (sender_id = auth.uid() or receiver_id = auth.uid());

drop policy if exists "Insert own messages" on public.messages;
create policy "Insert own messages"
  on public.messages
  for insert
  to authenticated
  with check (sender_id = auth.uid());

-- Helper view: conversations derived from accepted interests (one per pair)
create view if not exists public.conversations as
select
  i.id as conversation_id,
  i.sender_id,
  i.receiver_id,
  i.updated_at as started_at
from public.interests i
where i.status = 'accepted';

-- RLS for conversations view via messages/interests policies already control access

