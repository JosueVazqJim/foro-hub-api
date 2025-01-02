package foro.hub.api.domain.respuesta;

import java.time.LocalDateTime;

public record DatosListadoRespuesta(String mensaje, LocalDateTime fecha_creacion, Long idUsuario,
                                    Long idTopico) {

	public DatosListadoRespuesta(Respuesta respuesta) {
		this(respuesta.getMensaje(), respuesta.getFechaCreacion(), respuesta.getUsuario().getId(),
				respuesta.getTopico().getId());
	}
}
