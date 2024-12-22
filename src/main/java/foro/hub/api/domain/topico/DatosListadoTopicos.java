package foro.hub.api.domain.topico;

import java.time.LocalDateTime;

public record DatosListadoTopicos(String titulo, String mensaje, LocalDateTime fecha_creacion, Long idUsuario,
                                  String status, Long idCurso) {

	public DatosListadoTopicos(Topico topico) {
		this(topico.getTitulo(), topico.getMensaje(), topico.getFechaCreacion(), topico.getUsuario().getId(),
				topico.getStatus(), topico.getCurso().getId());
	}
}
