-- Fix the user_id sequence to be in sync with the actual table data
-- This prevents duplicate key errors when creating new users after deletions

SELECT setval('car_dealership_user_user_id_seq', COALESCE((SELECT MAX(user_id) FROM car_dealership_user), 0) + 1, false);
