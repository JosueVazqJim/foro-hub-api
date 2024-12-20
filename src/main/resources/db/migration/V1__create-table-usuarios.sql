create table usuarios (
    id bigint not null primary key auto_increment,
    nombre varchar(255) not null,
    correoElectronico varchar(255) not null unique,
    contrasena varchar(255) not null
);