package foro.hub.api.domain.topico;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.curso.CursoRepository;
import foro.hub.api.domain.topico.validaciones.IValidadoresTopicos;
import foro.hub.api.domain.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LogicaTopico {

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CursoRepository cursoRepository;

	//validadores
	@Autowired
	private List<IValidadoresTopicos> validadores;

	public DatosResTopico registrar(DatosRegistroTopico datos) {

		if (!usuarioRepository.existsById(datos.idUsuario())) {
			throw new ValidacionException("No existe un usuario con el id indicado");
		}

		if (!cursoRepository.existsById(datos.idCurso())) {
			throw new ValidacionException("No existe un curso con el id indicado");
		}

		//ejecuta las validaciones
		validadores.forEach(v -> v.validar(datos));

		var usuario = usuarioRepository.findById(datos.idUsuario()).get();
		var curso = cursoRepository.findById(datos.idCurso()).get();
		var topico = new Topico(datos, usuario, curso);
		topicoRepository.save(topico);

		return new DatosResTopico(topico);
	}

	public DatosResTopico actualizar(@Valid Long id, @Valid DatosActualizarTopico datosActualizarTopico) {
		var optionalTopico = topicoRepository.findByIdAndEliminadoFalse(id);
		if (optionalTopico.isPresent()) {
			var topico = optionalTopico.get();
			if (!usuarioRepository.existsById(datosActualizarTopico.idUsuario())) {
				throw new ValidacionException("No existe un usuario con el id indicado");
			}

			if (!cursoRepository.existsById(datosActualizarTopico.idCurso())) {
				throw new ValidacionException("No existe un curso con el id indicado");
			}
			boolean duplicado = topicoRepository.findByTituloAndMensaje(datosActualizarTopico.titulo(),
					datosActualizarTopico.mensaje());
			if (duplicado) {
				throw new ValidacionException("Ya existe un topico con el mismo titulo y mensaje");
			}
			topico.actualizar(datosActualizarTopico);
			topicoRepository.save(topico);
			return new DatosResTopico(topico);
		} else {
			throw new NoSuchElementException("No existe un tópico con el id indicado");
		}
	}

	public void eliminar(@Valid Long id) {
		var optionalTopico = topicoRepository.findByIdAndEliminadoFalse(id);
		if (optionalTopico.isPresent()) {
			var topico = optionalTopico.get();
			topico.setEliminado(true);
			topicoRepository.save(topico);
		} else {
			throw new ValidacionException("No existe un tópico con el id indicado");
		}
	}
}
