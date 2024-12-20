alter table topicos
    add constraint fk_usuario
    foreign key (id_usuario)
    references usuarios(id);

alter table topicos
    add constraint fk_curso
    foreign key (id_curso)
    references cursos(id);