insert into parking.users (name, email, password)
values ('John Doe', 'johndoe@gmail.com', '12345'),
       ('Mike Smith', 'mikesmith@yahoo.com', '12345'),
       ('Sam Nilson', 'samnilson@icloud.com', '12345');

insert into parking.cars (number)
values ('GT784W88'),
       ('QW568F33'),
       ('KJ156H51');

insert into parking.reservations (time_from, time_to)
values ('2023-01-29 12:00:00', '2023-01-23 14:00:00'),
       ('2023-01-29 10:54:44', '2023-01-30 12:00:00');

insert into zones(number)
values (1),
       (2),
       (3);

insert into parking.places (number, status)
VALUES (1, 'FREE'),
       (2, 'FREE'),
       (3, 'OCCUPIED'),
       (1, 'DISABLE'),
       (2, 'OCCUPIED'),
       (1, 'FREE'),
       (2, 'FREE');

insert into parking.users_cars(user_id, car_id)
values (1, 1),
       (1, 2),
       (2, 3);

insert into parking.users_roles (user_id, role)
values (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER'),
       (2, 'ROLE_USER'),
       (3, 'ROLE_USER');

insert into parking.zones_places (zone_id, place_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (2, 4),
       (2, 5),
       (3, 6),
       (3, 7);

insert into parking.reservations_places (reservation_id, place_id)
values (1, 3),
       (2, 5);

insert into parking.cars_places(car_id, place_id)
values (2, 5),
       (3, 3);