ALTER TABLE service_mechanic
    ADD COLUMN quantity INT DEFAULT 1;

-- Update existing records to have quantity = 1
UPDATE service_mechanic SET quantity = 1 WHERE quantity IS NULL;
