insert into users (name, email, password)
values ('John Doe', 'johndoe@gmail.com', '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e'),
       ('Mike Smith', 'mikesmith@yahoo.com', '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e'),
       ('Sam Nilson', 'samnilson@icloud.com', '$2a$10$rG8IgrbLTcX5lsWIiNhmCeznXeVZMur6d3ss.YGPMhIEWQUJ7c63e');

insert into cars (number)
values ('G784TW777'),
       ('Q568WF33'),
       ('K156JH31');

insert into zones(number)
values (1),
       (2),
       (3);

insert into places (number, status)
VALUES (1, 'FREE'),
       (2, 'FREE'),
       (3, 'OCCUPIED'),
       (1, 'DISABLE'),
       (2, 'OCCUPIED'),
       (1, 'FREE'),
       (2, 'FREE');

insert into users_cars(user_id, car_id)
values (1, 1),
       (1, 2),
       (2, 3);

insert into users_roles (user_id, role)
values (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER'),
       (2, 'ROLE_USER'),
       (3, 'ROLE_USER');

insert into zones_places (zone_id, place_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (2, 4),
       (2, 5),
       (3, 6),
       (3, 7);

insert into reservations (time_from, time_to, user_id, car_id, zone_id, place_id)
values ('2024-01-29 12:00:00', '2024-01-23 14:00:00', 1, 2, 1, 3),
       ('2024-01-29 10:54:44', '2024-01-30 12:00:00', 2, 3, 2, 5);
