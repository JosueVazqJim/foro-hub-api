package foro.hub.api.domain.perfil;

public record DatosListadoPerfiles(Long id, String nombre, Long idUsuario) {
	public DatosListadoPerfiles(Perfil perfil) {
		this(perfil.getId(), perfil.getNombre(), perfil.getUsuario().getId());
	}
}
