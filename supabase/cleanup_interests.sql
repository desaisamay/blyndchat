-- Cleanup duplicate accepted interests and enforce single thread per pair
-- Run this in the Supabase SQL editor.

-- 1) Identify canonical conversation per pair (earliest accepted)
DROP TABLE IF EXISTS tmp_pairs;
DROP TABLE IF EXISTS tmp_canonical;
DROP TABLE IF EXISTS tmp_dups;

CREATE TEMP TABLE tmp_pairs AS
SELECT
  LEAST(sender_id, receiver_id) AS a,
  GREATEST(sender_id, receiver_id) AS b,
  id,
  created_at,
  ROW_NUMBER() OVER (
    PARTITION BY LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id)
    ORDER BY created_at ASC, id ASC
  ) AS rn
FROM public.interests
WHERE status = 'accepted';

CREATE TEMP TABLE tmp_canonical AS
SELECT a, b, id AS canonical_id
FROM tmp_pairs
WHERE rn = 1;

CREATE TEMP TABLE tmp_dups AS
SELECT a, b, id
FROM tmp_pairs
WHERE rn > 1;
-- 2) Migrate messages from duplicate conversations to canonical
UPDATE public.messages m
SET conversation_id = c.canonical_id
FROM tmp_dups d
JOIN tmp_canonical c ON c.a = d.a AND c.b = d.b
WHERE m.conversation_id = d.id;

-- 3) Archive duplicate interests so the unique index can be created
UPDATE public.interests i
SET status = 'rejected'
FROM tmp_dups d
WHERE i.id = d.id;

-- 4) Create unique partial index (enforces only one accepted interest per pair)
CREATE UNIQUE INDEX IF NOT EXISTS interests_pair_once_accepted
ON public.interests (
  LEAST(sender_id, receiver_id),
  GREATEST(sender_id, receiver_id)
)
WHERE status = 'accepted';

-- 5) Ensure Realtime publication includes messages
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_publication_tables
    WHERE pubname = 'supabase_realtime'
      AND schemaname = 'public'
      AND tablename = 'messages'
  ) THEN
    ALTER PUBLICATION supabase_realtime ADD TABLE public.messages;
  END IF;
END $$;

-- Optional: drop temp tables
DROP TABLE IF EXISTS tmp_dups;
DROP TABLE IF EXISTS tmp_canonical;
DROP TABLE IF EXISTS tmp_pairs;
