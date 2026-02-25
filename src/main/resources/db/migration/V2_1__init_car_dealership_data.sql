insert into SALESMAN (name, surname, pesel)
values
('Piotr', 'Sprzedawca', '61012164221'),
('Stefan', 'Samochodowy', '81110679371'),
('Krzysztof', 'Komisowski', '82032491827'),
('Albert', 'Kierownica', '76091834562');

insert into MECHANIC (name, surname, pesel)
values
('Arkadiusz', 'Serwisowy', '74022659873'),
('Andrzej', 'Naprawa', '82041197536'),
('Grzegorz', 'Klucznik', '69071583421');

insert into CAR_TO_BUY (vin, brand, model, production_year, color, price)
values
('WAUZZZ8K9DA123456', 'Audi', 'A4', '2019', 'gray', '75000'),
('WAUZZZ8K1FA654321', 'Audi', 'A6', '2021', 'black', '135000'),
('WAUZZZFY4K2123456', 'Audi', 'Q5', '2020', 'white', '145000'),

('WBA1A11090E123456', 'BMW', '3 Series', '2018', 'blue', '89000'),
('WBA5A31070F654321', 'BMW', '5 Series', '2019', 'black', '125000'),
('WBAFR91030C789012', 'BMW', 'X3', '2020', 'silver', '158000'),

('WDB2040481A654321', 'Mercedes-Benz', 'C-Class', '2021', 'silver', '115000'),
('WDD2120481A987654', 'Mercedes-Benz', 'E-Class', '2019', 'black', '139000'),
('WDC2539051F321654', 'Mercedes-Benz', 'GLC', '2022', 'white', '189000'),

('WVWZZZ1KZAW987654', 'Volkswagen', 'Golf', '2020', 'blue', '68000'),
('WVWZZZ3CZEE456789', 'Volkswagen', 'Passat', '2018', 'gray', '72000'),
('WV1ZZZ7HZJ1234567', 'Volkswagen', 'Transporter', '2021', 'white', '155000'),

('VF1RFB00565432109', 'Renault', 'Megane', '2018', 'white', '42000'),
('VF1RFE00512345678', 'Renault', 'Kadjar', '2019', 'red', '59000'),
('VF1RFD00798765432', 'Renault', 'Clio', '2022', 'orange', '61000'),

('JN1AZ0CP6BT012345', 'Nissan', 'Qashqai', '2017', 'red', '53000'),
('JN1TANT32U0123456', 'Nissan', 'X-Trail', '2020', 'black', '97000'),
('JN1BJ0HR3LM654321', 'Nissan', 'Juke', '2021', 'yellow', '83000');

insert into SERVICE (service_code, description, price)
values
('SRV-001', 'Computer diagnostics', '200.00'),
('SRV-002', 'Brake pads replacement (per axle)', '300.00'),
('SRV-003', 'Wheel alignment (3D)', '250.00'),
('SRV-004', 'Timing belt replacement', '1500.00'),
('SRV-005', 'Air conditioning service (refill + disinfection)', '350.00'),

('SRV-006', 'Shock absorbers replacement (per axle)', '500.00'),
('SRV-007', 'Turbocharger regeneration (labor)', '900.00'),
('SRV-008', 'Clutch replacement', '1400.00'),
('SRV-009', 'Full brake service front + rear (labor)', '600.00'),
('SRV-010', 'Radiator replacement', '600.00'),

('SRV-011', 'DPF cleaning (removal + installation)', '1000.00'),
('SRV-012', 'Spark plugs replacement', '200.00'),
('SRV-013', 'Comprehensive suspension service', '800.00'),
('SRV-014', 'Battery replacement', '100.00'),
('SRV-015', 'Extended pre-purchase inspection', '450.00');

insert into PART (serial_number, description, price)
values
('PRT-1001', 'Brake pads set (per axle)', '350.00'),
('PRT-1002', 'Brake discs set (per axle)', '600.00'),
('PRT-1003', 'Timing belt kit with water pump', '1200.00'),
('PRT-1004', 'Cabin air filter', '80.00'),

('PRT-1005', 'Front shock absorber (single unit)', '400.00'),
('PRT-1006', 'Clutch kit (with slave cylinder)', '1800.00'),
('PRT-1007', 'Radiator', '800.00'),
('PRT-1008', 'Spark plugs set', '250.00'),

('PRT-1009', '74Ah AGM battery', '650.00'),
('PRT-1010', 'Wheel bearing (single unit)', '300.00'),
('PRT-1011', 'Turbocharger (remanufactured)', '2500.00'),
('PRT-1012', 'DPF filter (aftermarket)', '2000.00');