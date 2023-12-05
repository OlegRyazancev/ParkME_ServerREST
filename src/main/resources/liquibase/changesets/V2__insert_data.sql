insert into users (name, email, password)
values ('John Doe', 'johndoe@gmail.com',
        '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e'),
       ('Mike Smith', 'mikesmith@yahoo.com',
        '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e'),
       ('Sam Nilson', 'samnilson@icloud.com',
        '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e'),
       ('Emily Johnson', 'emilyjohnson@gmail.com',
        '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e'),
       ('Alex Rodriguez', 'alexrodriguez@yahoo.com',
        '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e'),
       ('Jessica White', 'jessicawhite@hotmail.com',
        '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e'),
       ('Ryan Carter', 'ryancarter@gmail.com',
        '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e');

insert into cars (number)
values ('G784TW777'),
       ('Q568WF33'),
       ('K156JH31'),
       ('X000XX00'),
       ('A123BC45'),
       ('M789NP12'),
       ('R456GH78'),
       ('S901KL23'),
       ('T567UV89'),
       ('W234XY90'),
       ('Z876IJ01'),
       ('B345DE67'),
       ('F890QR23'),
       ('N678ST45');

insert into zones(number)
values (1),
       (2),
       (3),
       (4),
       (5),
       (6);

insert into places (number, status)
VALUES (1, 'FREE'),
       (2, 'FREE'),
       (3, 'FREE'),
       (4, 'FREE'),
       (5, 'FREE'),
       (6, 'FREE'),
       (7, 'FREE'),
       (8, 'FREE'),
       (9, 'FREE'),
       (10, 'FREE'),
       (11, 'FREE'),
       (12, 'FREE'),

       (1, 'FREE'),
       (2, 'FREE'),
       (3, 'FREE'),
       (4, 'OCCUPIED'),
       (5, 'FREE'),
       (6, 'FREE'),
       (7, 'FREE'),

       (1, 'FREE'),
       (2, 'FREE'),
       (3, 'FREE'),
       (4, 'FREE'),
       (5, 'FREE'),
       (6, 'FREE'),
       (7, 'FREE'),
       (8, 'FREE'),
       (9, 'FREE'),

       (1, 'FREE'),
       (2, 'FREE'),
       (3, 'FREE'),
       (4, 'FREE'),
       (5, 'FREE'),
       (6, 'FREE'),
       (7, 'FREE'),
       (8, 'FREE'),
       (9, 'FREE'),
       (10, 'FREE'),
       (11, 'FREE'),

       (1, 'FREE'),
       (2, 'FREE'),
       (3, 'FREE'),
       (4, 'FREE'),
       (5, 'FREE'),
       (6, 'OCCUPIED'),

       (1, 'OCCUPIED'),
       (2, 'FREE'),
       (3, 'FREE'),
       (4, 'FREE');

insert into users_cars(user_id, car_id)
values (1, 1),
       (1, 2),
       (2, 3),
       (2, 4),
       (3, 5),
       (3, 6),
       (4, 7),
       (4, 8),
       (5, 9),
       (5, 10),
       (6, 11),
       (6, 12),
       (6, 13),
       (6, 14);

insert into users_roles (user_id, role)
values (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER'),
       (2, 'ROLE_USER'),
       (3, 'ROLE_USER'),
       (4, 'ROLE_USER'),
       (5, 'ROLE_USER'),
       (6, 'ROLE_USER'),
       (7, 'ROLE_USER');


insert into zones_places (zone_id, place_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 7),
       (1, 8),
       (1, 9),
       (1, 10),
       (1, 11),
       (1, 12),

       (2, 13),
       (2, 14),
       (2, 15),
       (2, 16),
       (2, 17),
       (2, 18),
       (2, 19),

       (3, 20),
       (3, 21),
       (3, 22),
       (3, 23),
       (3, 24),
       (3, 25),
       (3, 26),
       (3, 27),
       (3, 28),

       (4, 29),
       (4, 30),
       (4, 31),
       (4, 32),
       (4, 33),
       (4, 34),
       (4, 35),
       (4, 36),
       (4, 37),
       (4, 38),
       (4, 39),

       (5, 40),
       (5, 41),
       (5, 42),
       (5, 43),
       (5, 44),
       (5, 45),

       (6, 46),
       (6, 47),
       (6, 48),
       (6, 49);


insert into reservations (time_from, time_to, status, user_id, car_id, zone_id,
                          place_id)
values
    -- 3 start in past and end in future
    ('2023-05-10 08:30:00', '2024-06-15 18:45:00', 'ACTIVE', 1, 1, 2, 16),
    ('2022-08-20 14:00:00', '2024-01-05 10:30:00', 'ACTIVE', 4, 7, 5, 45),
    ('2023-11-03 09:15:00', '2024-09-22 16:00:00', 'ACTIVE', 3, 6, 6, 46),

-- 5 start in past and end in past
    ('2022-03-15 16:45:00', '2022-04-10 11:30:00', 'COMPLETED', 5, 9, 3, 24),
    ('2021-07-28 12:00:00', '2021-08-15 09:00:00', 'CANCELED', 1, 1, 2, 16),
    ('2020-12-05 20:30:00', '2021-02-10 14:45:00', 'COMPLETED', 2, 4, 1, 6),
    ('2022-01-10 08:00:00', '2022-01-20 18:30:00', 'CANCELED', 4, 8, 5, 42),
    ('2021-04-22 09:45:00', '2021-05-01 17:00:00', 'COMPLETED', 3, 5, 4, 39),

-- 2 start and end in future
    ('2025-02-28 10:00:00', '2025-03-10 16:15:00', 'PLANNED', 2, 1, 2, 16),
    ('2025-05-22 10:00:00', '2025-07-10 16:15:00', 'PLANNED', 4, 7, 2, 16),
    ('2024-11-15 15:30:00', '2025-01-02 12:45:00', 'PLANNED', 1, 2, 2, 16);
