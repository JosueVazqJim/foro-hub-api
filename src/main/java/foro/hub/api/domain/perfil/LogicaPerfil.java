package foro.hub.api.domain.perfil;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.perfil.validaciones.IValidadoresPerfiles;
import foro.hub.api.domain.usuario.Usuario;
import foro.hub.api.domain.usuario.UsuarioRepository;
import foro.hub.api.domain.usuario.validaciones.IValidadoresUsuarios;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LogicaPerfil {

	@Autowired
	private PerfilRepository perfilRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	//validadores
	@Autowired
	private List<IValidadoresPerfiles> validadores;

	public DatosResPerfil registrar(DatosRegistroPerfil datos) {

		Optional<Usuario> usuario = usuarioRepository.findByIdAndEliminadoFalse(datos.idUsuario());

		if (usuario.isEmpty()) {
			throw new ValidacionException("No existe un Usuario con el id indicado");
		}
		//ejecuta las validaciones
		validadores.forEach(v -> v.validar(datos));
		Perfil perfil = perfilRepository.save(new Perfil(datos, usuario.get()));
		return new DatosResPerfil(perfil.getId(), perfil.getNombre(), perfil.getUsuario().getId());
	}

	public DatosResPerfil actualizar(@Valid Long id, @Valid DatosActualizarPerfil datosActualizarPerfil) {
		var optionalPerfil = perfilRepository.findByIdAndEliminadoFalse(id);
		if (optionalPerfil.isPresent()) {
			var perfil = optionalPerfil.get();
			if (!datosActualizarPerfil.nombre().isBlank()) {
				System.out.println("Si hay nombre");
				System.out.println("perfil.getNombre(): " + perfil.getNombre());
				boolean duplicado = perfilRepository.findByNombreAndIdUsuario(datosActualizarPerfil.nombre(), perfil.getUsuario().getId());
				if (duplicado) {
					throw new ValidacionException("El usuario ya tiene un Perfil con el mismo nombre");
				}
				perfil.actualizar(datosActualizarPerfil);
				perfilRepository.save(perfil);
			}
			return new DatosResPerfil(perfil.getId(), perfil.getNombre(), perfil.getUsuario().getId());
		} else {
			throw new NoSuchElementException("No existe un Perfil con el id indicado");
		}
	}

	public void eliminar(@Valid Long id) {
		var optionalPerfil = perfilRepository.findByIdAndEliminadoFalse(id);
		if (optionalPerfil.isPresent()) {
			var perfil = optionalPerfil.get();
			perfil.setEliminado(true);
			perfilRepository.save(perfil);
			//perfilRepository.deleteById(id);
		} else {
			throw new ValidacionException("No existe un Perfil con el id indicado");
		}
	}

	private String encriptarBCrypt(String clave) {
		var encoder = new BCryptPasswordEncoder();
		return encoder.encode(clave);
	}
}
