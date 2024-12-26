package foro.hub.api.domain.usuario;

import foro.hub.api.domain.topico.Topico;

public record DatosListadoUsuarios(String nombre, String email) {
	public DatosListadoUsuarios(Usuario usuario) {
		this(usuario.getNombre(), usuario.getEmail());
	}
}
