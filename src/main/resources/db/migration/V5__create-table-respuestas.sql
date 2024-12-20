create table respuestas (
    id bigint not null primary key auto_increment,
    mensaje text not null,
    fechaCreacion datetime not null,
    solucion boolean not null,
    id_usuario bigint not null,
    id_topico bigint not null
);