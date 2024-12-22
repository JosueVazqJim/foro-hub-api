package foro.hub.api.domain.topico;

public record DatosResTopico(Long id, String titulo, String mensaje) {
	public DatosResTopico(Topico topico) {
		this(topico.getId(), topico.getTitulo(), topico.getMensaje());
	}
}