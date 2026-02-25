-- Add password_change_required column to car_dealership_user table
-- This flag is set to true when admin resets user's password
-- forcing the user to change their password on next login

ALTER TABLE car_dealership_user
ADD COLUMN IF NOT EXISTS password_change_required BOOLEAN NOT NULL DEFAULT FALSE;
