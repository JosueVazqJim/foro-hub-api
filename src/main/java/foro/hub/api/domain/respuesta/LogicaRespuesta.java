package foro.hub.api.domain.respuesta;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.topico.*;
import foro.hub.api.domain.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class LogicaRespuesta {

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private RespuestaRepository respuestaRepository;


	public DatosResRespuesta registrar(DatosRegistroRespuesta datos) {

		if (!usuarioRepository.existsByIdAndEliminadoFalse(datos.idUsuario())) {
			throw new ValidacionException("No existe un usuario con el id indicado");
		}

		if (!topicoRepository.existsByIdAndEliminadoFalse(datos.idTopico())) {
			throw new ValidacionException("No existe un topico con el id indicado");
		}

		if (respuestaRepository.existsByMensajeAndUsuarioIdAndTopicoIdAndEliminadoFalse(datos.mensaje(), datos.idUsuario(), datos.idTopico())) {
			throw new ValidacionException("Entrada duplicada");
		}

		var usuario = usuarioRepository.findById(datos.idUsuario()).get();
		var topico = topicoRepository.findById(datos.idTopico()).get();
		var respuesta = new Respuesta(datos, usuario, topico);
		respuestaRepository.save(respuesta);

		return new DatosResRespuesta(respuesta);
	}

	public DatosResRespuesta actualizar(@Valid Long id, @Valid DatosActualizarRespuesta datos) {
		var optionalRespuesta = respuestaRepository.findByIdAndEliminadoFalse(id);
		if (optionalRespuesta.isPresent()) {
			var respuesta = optionalRespuesta.get();
			if (datos.mensaje() == null || datos.mensaje().isEmpty() || datos.mensaje().isBlank()) {
				return new DatosResRespuesta(respuesta);
			}
			if (respuestaRepository.existsByMensajeAndUsuarioIdAndTopicoIdAndEliminadoFalse(datos.mensaje(),
					respuesta.getUsuario().getId(), respuesta.getTopico().getId())) {
				throw new ValidacionException("Entrada duplicada");
			}
			respuesta.actualizar(datos);
			respuestaRepository.save(respuesta);
			return new DatosResRespuesta(respuesta);
		} else {
			throw new NoSuchElementException("No existe Respuesta con el id indicado");
		}
	}

	public void eliminar(@Valid Long id) {
		var optionalRespuesta = respuestaRepository.findByIdAndEliminadoFalse(id);
		if (optionalRespuesta.isPresent()) {
			var respuesta = optionalRespuesta.get();
			respuesta.setEliminado(true);
			respuestaRepository.save(respuesta);
			//respuestaRepository.delete(respuesta);
		} else {
			throw new ValidacionException("No existe una Respuesta con el id indicado");
		}
	}
}
