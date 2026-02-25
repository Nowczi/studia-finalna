-- Make user_id nullable to allow unlinking when deleting users
ALTER TABLE mechanic
ALTER COLUMN user_id DROP NOT NULL;

ALTER TABLE salesman
ALTER COLUMN user_id DROP NOT NULL;
