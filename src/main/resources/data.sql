create table IF NOT EXISTS TEXTS (
     uid UUID,
     id int,
     ts timestamp,
     channel varchar(255),
     fromUser varchar(255),
     forUser varchar(255),
     text varchar(255)
);

create table IF NOT EXISTS AUDIOS (
    uid UUID,
    id int,
    ts timestamp,
    channel varchar(255),
    fromUser varchar(255),
    forUser varchar(255),
    type varchar(255),
    codec varchar(255),
    codecHeader varchar(255),
    packetDuration int
);

create table IF NOT EXISTS LOCATIONS (
    uid UUID,
    id int,
    ts timestamp,
    channel varchar(255),
    fromUser varchar(255),
    forUser varchar(255),
    formattedAddress varchar(255),
    longitude double,
    latitude double,
    accuracy double
);

create table IF NOT EXISTS IMAGES (
    uid UUID,
    id int,
    ts timestamp,
    channel varchar(255),
    fromUser varchar(255),
    forUser varchar(255),
    type varchar(255),
    source varchar(255),
    height int,
    width int
);
