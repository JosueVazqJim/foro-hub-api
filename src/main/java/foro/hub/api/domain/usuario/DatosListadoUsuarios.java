package foro.hub.api.domain.usuario;

import foro.hub.api.domain.topico.Topico;

public record DatosListadoUsuarios(Long id, String nombre, String email) {
	public DatosListadoUsuarios(Usuario usuario) {
		this(usuario.getId(), usuario.getNombre(), usuario.getEmail());
	}
}
