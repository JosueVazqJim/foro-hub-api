create table perfiles (
    id bigint not null primary key auto_increment,
    nombre varchar(255) not null,
    id_usuario bigint not null,

    foreign key (id_usuario) references usuarios(id)
);