alter table usuarios add column eliminado boolean not null default false;
alter table perfiles add column eliminado boolean not null default false;
alter table cursos add column eliminado boolean not null default false;
alter table topicos add column eliminado boolean not null default false;
alter table respuestas add column eliminado boolean not null default false;