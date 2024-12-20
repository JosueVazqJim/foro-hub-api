alter table respuestas
    add constraint fk_topico
    foreign key (id_topico)
    references topicos(id);