alter table respuestas
    add constraint fk_usuario_respuestas
    foreign key (id_usuario)
    references usuarios(id);