package foro.hub.api.domain.respuesta;

import foro.hub.api.domain.topico.Topico;

public record DatosResRespuesta(Long id, String mensaje, Long idUsuario, Long idTopico) {
	public DatosResRespuesta(Respuesta respuesta) {
		this(respuesta.getId(), respuesta.getMensaje(), respuesta.getUsuario().getId(), respuesta.getTopico().getId());
	}
}