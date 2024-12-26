package foro.hub.api.domain.usuario;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	UserDetails findByNombre(String nombre);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM Usuario u WHERE u.email = :email AND u.eliminado = false")
	boolean findByEmail(String email);

	@Query("SELECT u FROM Usuario u WHERE u.id = :id AND u.eliminado = false")
	Optional<Usuario> findByIdAndEliminadoFalse(@NotNull Long id);
}
