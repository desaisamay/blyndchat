-- Messages table backing chats. Run this in Supabase SQL editor.
-- Schema: public

create table if not exists public.messages (
  id uuid primary key default gen_random_uuid(),
  conversation_id uuid not null,
  sender_id uuid not null,
  receiver_id uuid not null,
  content text not null,
  created_at timestamptz not null default now()
);

-- Optional: index for faster retrieval by conversation
create index if not exists messages_conversation_id_idx on public.messages (conversation_id);
create index if not exists messages_created_at_idx on public.messages (created_at);

-- Row Level Security
alter table public.messages enable row level security;

-- Policy: participants of the conversation can read messages
-- Recreate read policy (Postgres doesn't support IF NOT EXISTS on policies)
drop policy if exists "read messages if participant" on public.messages;
create policy "read messages if participant"
  on public.messages for select
  using (
    -- a participant is either sender or receiver
    (auth.uid() = sender_id) or (auth.uid() = receiver_id)
  );

-- Policy: participants can insert messages they send
-- Recreate insert policy
drop policy if exists "insert messages by sender" on public.messages;
create policy "insert messages by sender"
  on public.messages for insert
  with check (
    -- only allow inserting messages where current user is sender
    auth.uid() = sender_id
  );

-- Policy: disallow updates/deletes for now (immutable chat history)
revoke update on public.messages from authenticated;
revoke delete on public.messages from authenticated;

-- Grants
grant select, insert on public.messages to authenticated;

-- Note: conversation_id should reference an accepted interest row id.
-- If you want FK, uncomment next line after ensuring interests table exists:
-- alter table public.messages add constraint messages_conversation_fk foreign key (conversation_id) references public.interests (id);
