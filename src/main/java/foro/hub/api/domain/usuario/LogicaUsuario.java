package foro.hub.api.domain.usuario;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.curso.CursoRepository;
import foro.hub.api.domain.topico.*;
import foro.hub.api.domain.topico.validaciones.IValidadoresTopicos;
import foro.hub.api.domain.usuario.validaciones.IValidadoresUsuarios;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LogicaUsuario {

	@Autowired
	private UsuarioRepository usuarioRepository;

	//validadores
	@Autowired
	private List<IValidadoresUsuarios> validadores;

	public DatosResUsuario registrar(DatosRegistroUsuario datos) {
		//ejecuta las validaciones
		validadores.forEach(v -> v.validar(datos));
		var claveEncriptada = encriptarBCrypt(datos.contrasena());
		Usuario usuario = usuarioRepository.save(new Usuario(datos, claveEncriptada));
		return new DatosResUsuario(usuario.getId(), usuario.getNombre(), usuario.getEmail());
	}

	public DatosResUsuario actualizar(@Valid Long id, @Valid DatosActualizarUsuario datosActualizarUsuario) {
		var optionalUsuario = usuarioRepository.findByIdAndEliminadoFalse(id);
		if (optionalUsuario.isPresent()) {
			var usuario = optionalUsuario.get();
			boolean duplicado = usuarioRepository.existsByEmail(datosActualizarUsuario.email());
			if (duplicado) {
				throw new ValidacionException("Ya existe un Perfil con el mismo email");
			}
			if (datosActualizarUsuario.contrasena() != null) {
				usuario.actualizar(datosActualizarUsuario, encriptarBCrypt(datosActualizarUsuario.contrasena()));
			} else {
				usuario.actualizar(datosActualizarUsuario);
			}
			usuarioRepository.save(usuario);
			return new DatosResUsuario(usuario.getId(), usuario.getNombre(), usuario.getEmail());
		} else {
			throw new NoSuchElementException("No existe un Perfil con el id indicado");
		}
	}

	public void eliminar(@Valid Long id) {
		var optionalUsuario = usuarioRepository.findByIdAndEliminadoFalse(id);
		if (optionalUsuario.isPresent()) {
			var usuario = optionalUsuario.get();
			usuario.setEliminado(true);
			usuarioRepository.save(usuario);
			//usuarioRepository.delete(optionalUsuario.get());
		} else {
			throw new ValidacionException("No existe un Perfil con el id indicado");
		}
	}

	private String encriptarBCrypt(String clave) {
		var encoder = new BCryptPasswordEncoder();
		return encoder.encode(clave);
	}
}
