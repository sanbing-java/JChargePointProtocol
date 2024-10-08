--
-- 抖音关注：程序员三丙
-- 知识星球：https://t.zsxq.com/j9b21
--

CREATE TABLE IF NOT EXISTS jcpp_user
(
    id               uuid                                not null
        constraint owner_pkey
            primary key,
    created_time     timestamp default CURRENT_TIMESTAMP not null,
    additional_info  jsonb,
    status           varchar(16)                         not null,
    user_name        varchar(255)                        not null,
    user_credentials jsonb                               not null,
    version          int                                 default 1
);

CREATE UNIQUE INDEX IF NOT EXISTS uni_user_name
    on jcpp_user (user_name);

CREATE TABLE IF NOT EXISTS jcpp_station
(
    id              uuid                                not null
        constraint station_pkey
            primary key,
    created_time    timestamp default CURRENT_TIMESTAMP not null,
    additional_info jsonb,
    station_name    varchar(255)                        not null,
    station_code    varchar(255)                        not null,
    owner_id        uuid                                not null,
    longitude       double precision                    not null,
    latitude        double precision                    not null,
    owner_type      varchar(16)                         not null,
    province        varchar(255),
    city            varchar(255),
    county          varchar(255),
    address         varchar(255),
    status          varchar(16)                         not null,
    version         int                                 default 1
);

CREATE UNIQUE INDEX IF NOT EXISTS uni_station_code
    on jcpp_station (station_code);

CREATE TABLE IF NOT EXISTS jcpp_pile
(
    id              uuid                                not null
        constraint pile_pkey
            primary key,
    created_time    timestamp default CURRENT_TIMESTAMP not null,
    additional_info jsonb,
    pile_name       varchar(255)                        not null,
    pile_code       varchar(255)                        not null,
    protocol        varchar(255)                        not null,
    station_id      uuid                                not null,
    owner_id        uuid                                not null,
    owner_type      varchar(16)                         not null,
    brand           varchar(255),
    model           varchar(255),
    manufacturer    varchar(255),
    status          varchar(16)                         not null,
    type            varchar(16)                         not null,
    version         int                                 default 1
);

CREATE UNIQUE INDEX IF NOT EXISTS uni_pile_code
    on jcpp_pile (pile_code);


CREATE TABLE IF NOT EXISTS jcpp_gun
(
    id                      uuid                                not null
        primary key,
    created_time            timestamp default CURRENT_TIMESTAMP not null,
    additional_info         varchar(255),
    gun_no                  varchar(255)                        not null,
    gun_name                varchar(255)                        not null,
    gun_code                varchar(255)                        not null,
    station_id              uuid                                not null,
    pile_id                 uuid                                not null,
    owner_id                uuid                                not null,
    owner_type              varchar(16)                         not null,
    run_status              varchar(16)                         not null,
    run_status_updated_time timestamp                           not null,
    opt_status              varchar(16)                         not null,
    version                 int                                 default 1
);

CREATE UNIQUE INDEX IF NOT EXISTS uni_gun_code
    on jcpp_gun (gun_code);

CREATE TABLE IF NOT EXISTS jcpp_order
(
    id                   uuid                                     not null
        primary key,
    internal_order_no    varchar(255)                             not null,
    external_order_no    varchar(255)                             not null,
    pile_order_No        varchar(255)                             not null,
    created_time         timestamp      default CURRENT_TIMESTAMP not null,
    additional_info      jsonb,
    updated_time         timestamp,
    cancelled_time       timestamp,
    status               varchar(16)                              not null,
    type                 varchar(16)                              not null,
    creator_id           uuid                                     not null,
    station_id           uuid                                     not null,
    pile_id              uuid                                     not null,
    gun_id               uuid                                     not null,
    plate_no             varchar(64),
    settlement_amount    numeric(16, 8)  default 0                not null,
    settlement_details   jsonb,
    electricity_quantity numeric(16, 8) default 0                 not null
);

CREATE UNIQUE INDEX IF NOT EXISTS uni_internal_order_no
    on jcpp_order (internal_order_no);

CREATE UNIQUE INDEX IF NOT EXISTS uni_external_order_no
    on jcpp_order (external_order_no);

