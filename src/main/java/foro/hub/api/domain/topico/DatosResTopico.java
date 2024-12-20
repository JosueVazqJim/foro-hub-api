package foro.hub.api.domain.topico;

public record DatosResTopico(Long id, String titulo) {
	public DatosResTopico(Topico topico) {
		this(topico.getId(), topico.getTitulo());
	}
}