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

-- Optional: bucket for profile photos (create in Studio UI)
-- Use a public bucket named 'profiles' and store image public URLs in image_url column.
