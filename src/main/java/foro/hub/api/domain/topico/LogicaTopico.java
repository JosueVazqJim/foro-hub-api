package foro.hub.api.domain.topico;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.curso.CursoRepository;
import foro.hub.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogicaTopico {

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CursoRepository cursoRepository;

	public DatosResTopico registrar(DatosRegistroTopico datos) {

		if (!usuarioRepository.existsById(datos.idUsuario())) {
			throw new ValidacionException("No existe un paciente con el id indicado");
		}

		if (!cursoRepository.existsById(datos.idCurso())) {
			throw new ValidacionException("No existe un paciente con el id indicado");
		}

		var usuario = usuarioRepository.findById(datos.idUsuario()).get();
		var curso = cursoRepository.findById(datos.idCurso()).get();
		var topico = new Topico(datos, usuario, curso);
		topicoRepository.save(topico);

		return new DatosResTopico(topico);
	}
}
