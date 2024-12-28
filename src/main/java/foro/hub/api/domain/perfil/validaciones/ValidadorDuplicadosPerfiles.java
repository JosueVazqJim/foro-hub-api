package foro.hub.api.domain.perfil.validaciones;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.perfil.DatosRegistroPerfil;
import foro.hub.api.domain.perfil.PerfilRepository;
import foro.hub.api.domain.usuario.Usuario;
import foro.hub.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ValidadorDuplicadosPerfiles implements IValidadoresPerfiles {

	@Autowired
	private PerfilRepository perfilRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	public void validar(DatosRegistroPerfil datosRegistroPerfil) {

		Optional<Usuario> usuario = usuarioRepository.findById(datosRegistroPerfil.idUsuario());

		boolean duplicado = perfilRepository.findByNombreAndIdUsuario(datosRegistroPerfil.nombre(),
				datosRegistroPerfil.idUsuario());
		if (duplicado) {
			throw new ValidacionException("Ya existe un Perfil con el mismo nombre");
		}
	}
}
