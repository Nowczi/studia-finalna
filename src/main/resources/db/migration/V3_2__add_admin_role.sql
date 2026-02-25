-- Add ADMIN role
INSERT INTO car_dealership_role (role_id, role) VALUES (4, 'ADMIN');

-- Create default admin user (password: test)
INSERT INTO car_dealership_user (user_id, user_name, email, password, active)
VALUES (9, 'admin', 'admin@nowakowskicar.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);

-- Assign ADMIN role to admin user
INSERT INTO car_dealership_user_role (user_id, role_id) VALUES (9, 4);