package foro.hub.api.domain.topico.validaciones;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.topico.DatosRegistroTopico;
import foro.hub.api.domain.topico.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorDuplicados implements IValidadoresTopicos {

	@Autowired
	private TopicoRepository topicoRepository;

	public void validar(DatosRegistroTopico datosRegistroTopico) {
		//buscaoms si existe un topico con los mismos datos
		boolean duplicado = topicoRepository.findByTituloAndUsuarioId(datosRegistroTopico.titulo(), datosRegistroTopico.idUsuario());
		if (duplicado) {
			throw new ValidacionException("Ya existe un topico con el mismo titulo");
		}
	}
}
