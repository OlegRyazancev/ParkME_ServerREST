insert into users (name, email, password)
values ('Test1', 'test1@gmail.com', '$2a$10$1tkzMXIgpvoxImncG9MX9ObP79SR/jM9zBliIrHo6a66tvsgt/YW.'),
       ('Test2', 'test2@gmail.com', '$2a$10$1tkzMXIgpvoxImncG9MX9ObP79SR/jM9zBliIrHo6a66tvsgt/YW.');

insert into users_roles (user_id, role)
values (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER');

insert into cars (number)
values ('A000AA00'),
       ('B000BB00'),
       ('C000CC00');

insert into users_cars (user_id, car_id)
values (1, 1),
       (1, 2),
       (1, 3);

insert into zones(number)
values (1),
       (2),
       (3);

insert into places (number, status)
VALUES (1, 'FREE'),
       (2, 'FREE'),
       (3, 'OCCUPIED'),
       (1, 'DISABLE'),
       (2, 'FREE'),
       (1, 'FREE'),
       (2, 'OCCUPIED');

insert into zones_places (zone_id, place_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (2, 4),
       (2, 5),
       (3, 6),
       (3, 7);

insert into reservations (time_from, time_to, user_id, car_id, zone_id, place_id)
values ('2024-01-01 12:00:00', '2024-01-23 14:00:00', 1, 1, 1, 3),
       ('2025-01-29 10:54:44', '2025-01-30 12:00:00', 1, 2, 3, 7);
