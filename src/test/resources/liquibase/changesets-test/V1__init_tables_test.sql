create table if not exists users
(
    id       bigserial primary key,
    name     varchar(255) not null,
    email    varchar(255) not null unique,
    password varchar(255) not null
);

create table if not exists cars
(
    id     bigserial primary key,
    number varchar(255) not null unique
);

create table if not exists users_roles
(
    user_id bigint       not null,
    role    varchar(255) not null,
    primary key (user_id, role),
    constraint fk_users_roles_users foreign key (user_id) references users (id) on delete cascade on update no action
);

create table if not exists users_cars
(
    user_id bigint not null,
    car_id  bigint not null,
    primary key (user_id, car_id),
    constraint fk_users_cars_users foreign key (user_id) references users (id) on delete cascade on update no action,
    constraint fk_users_cars_cars foreign key (car_id) references cars (id) on delete cascade on update no action
);

create table if not exists zones
(
    id     bigserial primary key,
    number bigint not null unique
);

create table if not exists places
(
    id     bigserial primary key,
    number bigint       not null,
    status varchar(255) not null
);

create table if not exists zones_places
(
    zone_id  bigint not null,
    place_id bigint not null,
    primary key (zone_id, place_id),
    constraint fk_zones_places_zones foreign key (zone_id) references zones (id) on delete cascade on update no action,
    constraint fk_zones_places_places foreign key (place_id) references places (id) on delete cascade on update no action
);


create table if not exists reservations
(
    id        bigserial primary key,
    time_from timestamp    not null,
    time_to   timestamp    not null,
    status    varchar(255) not null,
    user_id   bigint       not null,
    car_id    bigint       not null,
    zone_id   bigint       not null,
    place_id  bigint       not null,

    constraint fk_reservations_users foreign key (user_id) references users (id) on delete cascade on update no action,
    constraint fk_reservations_cars foreign key (car_id) references cars (id) on delete cascade on update no action,
    constraint fk_reservations_zones foreign key (zone_id) references zones (id) on delete cascade on update no action,
    constraint fk_reservations_places foreign key (place_id) references places (id) on delete cascade on update no action
);











