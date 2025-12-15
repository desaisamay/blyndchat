# Supabase Profiles Setup

## 1) Create table
Run `schema.sql` in Supabase SQL editor:

- Creates `public.profiles` with common fields
- Enables RLS
- Adds a policy allowing `anon` to `SELECT` all rows (adjust as needed)

## 2) Storage for photos
- Create a public Storage bucket named `profiles`
- Upload images (e.g. `profiles/priya.jpg`)
- Use the public URL in `image_url`:
  `https://<PROJECT>.supabase.co/storage/v1/object/public/profiles/<filename>`

## 3) Bulk import
- Prepare your CSV using `profiles_template.csv`
- In Supabase → Table editor → `profiles` → Import data → Upload the CSV

## 4) RLS/Policies
- The provided policy allows any `anon` client to read rows. If you want to restrict visibility:
  - Remove the public policy and create auth-based policies.

## 5) App config
- The app reads from PostgREST at `https://<PROJECT>.supabase.co/rest/v1/profiles` using the anon key
- Ensure `SUPABASE_URL` and `SUPABASE_ANON_KEY` are set in `local.properties` and exposed in BuildConfig

## 6) Optional: private images
- If you want private buckets, don’t use public URLs. Instead, create signed URLs per image via Storage API and use them in the app.
