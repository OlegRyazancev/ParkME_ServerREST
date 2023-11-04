insert into users (name, email, password)
values ('testName', 'testEmail@gmail.com', 'testPassword');

insert into cars (number)
values ('test_car_number_1'),
       ('test_car_number_2');

insert into users_cars(user_id, car_id)
values ((SELECT id FROM users WHERE name = 'testName'), (SELECT id FROM cars WHERE number = 'test_car_number_1')),
       ((SELECT id FROM users WHERE name = 'testName'), (SELECT id FROM cars WHERE number = 'test_car_number_2'));