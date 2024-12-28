package foro.hub.api.domain.perfil;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Perfil p WHERE p.nombre = :nombre AND p.usuario.id = :idUsuario AND p.eliminado = false")
	boolean findByNombreAndIdUsuario(String nombre, Long idUsuario);

	@Query("SELECT p FROM Perfil p WHERE p.id = :id AND p.eliminado = false")
	Optional<Perfil> findByIdAndEliminadoFalse(@NotNull Long id);

	Page<Perfil> findByEliminadoFalse(Pageable pageable);
}
