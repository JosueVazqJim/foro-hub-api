package foro.hub.api.domain.usuario.validaciones;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.usuario.DatosRegistroUsuario;
import foro.hub.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorDuplicadosUsuarios implements IValidadoresUsuarios {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public void validar(DatosRegistroUsuario datosRegistroUsuario) {
		//buscaoms si existe un topico con los mismos datos
		boolean duplicado = usuarioRepository.findByEmail(datosRegistroUsuario.email());
		if (duplicado) {
			throw new ValidacionException("Ya existe un Usuario con el mismo email");
		}
	}
}
