create table topicos (
    id bigint not null primary key auto_increment,
    titulo varchar(255) not null,
    mensaje text not null,
    fechaCreacion datetime not null,
    status varchar(20) not null,
    id_usuario bigint not null,
    id_curso bigint not null
);