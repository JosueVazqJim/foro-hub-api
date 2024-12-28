package foro.hub.api.infra.security;

import foro.hub.api.domain.UsuarioInvalido;
import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Marca esta clase como un componente de servicio de Spring, lo
// que permite que Spring la detecte y la gestione como un bean
// El servicio se encargará de interactuar con los repositorios y de
// implementar la lógica necesaria para la autenticación..
public class AutenticacionService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	// Sobrescribe el metodo de la interfaz `UserDetailsService`, que es parte de Spring Security.
	// Este metodo se usa para cargar un usuario desde una fuente de datos (como una base de datos)
	// según su nombre de usuario.
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var usuario = usuarioRepository.findByNombre(username);
		if (usuario == null) {
			System.out.println("Perfil invalido");
			throw new UsuarioInvalido("Perfil no encontrado");
		}
		return usuario;
	}
}
