ALTER TABLE salesman
ADD COLUMN user_id INT,
ADD FOREIGN KEY (user_id) REFERENCES car_dealership_user (user_id);

ALTER TABLE mechanic
ADD COLUMN user_id INT,
ADD FOREIGN KEY (user_id) REFERENCES car_dealership_user (user_id);

insert into car_dealership_user (user_id, user_name, email, password, active) values (1, 'piotr_sprzedawca', 'piotr_sprzedawca@nowakowskicar.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);
insert into car_dealership_user (user_id, user_name, email, password, active) values (2, 'stefan_samochodowy', 'stefan_samochodowy@nowakowskicar.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);
insert into car_dealership_user (user_id, user_name, email, password, active) values (3, 'krzysztof_komisowski', 'krzysztof_komisowski@nowakowskicar.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);
insert into car_dealership_user (user_id, user_name, email, password, active) values (4, 'albert_kierownica', 'albert_kierownica@nowakowskicar.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);

insert into car_dealership_user (user_id, user_name, email, password, active) values (5, 'arkadiusz_serwisowy', 'arkadiusz_serwisowy@nowakowskicar.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);
insert into car_dealership_user (user_id, user_name, email, password, active) values (6, 'andrzej_naprawa', 'andrzej_naprawa@nowakowskicar.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);
insert into car_dealership_user (user_id, user_name, email, password, active) values (7, 'grzegorz_klucznik', 'grzegorz_klucznik@nowakowskicar.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);

insert into car_dealership_user (user_id, user_name, email, password, active) values (8, 'test_user', 'test_user@nowakowskicar.pl', '$2a$12$TwQsp1IusXTDl7LwZqL0qeu49Ypr6vRdEzRq2vAsgb.zvOtrnzm5G', true);

UPDATE salesman SET user_id = 1 WHERE pesel = '61012164221';
UPDATE salesman SET user_id = 2 WHERE pesel = '81110679371';
UPDATE salesman SET user_id = 3 WHERE pesel = '82032491827';
UPDATE salesman SET user_id = 4 WHERE pesel = '76091834562';

UPDATE mechanic SET user_id = 5 WHERE pesel = '74022659873';
UPDATE mechanic SET user_id = 6 WHERE pesel = '82041197536';
UPDATE mechanic SET user_id = 7 WHERE pesel = '69071583421';

insert into car_dealership_role (role_id, role) values (1, 'SALESMAN'), (2, 'MECHANIC'), (3, 'REST_API');

insert into car_dealership_user_role (user_id, role_id) values (1, 1), (2, 1), (3, 1), (4, 1);
insert into car_dealership_user_role (user_id, role_id) values (5, 2), (6, 2), (7, 2);
insert into car_dealership_user_role (user_id, role_id) values (8, 3);

ALTER TABLE salesman
ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE mechanic
ALTER COLUMN user_id SET NOT NULL;