package foro.hub.api.domain.topico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
	@Query(""" 
		SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END
		FROM Topico t
		WHERE t.titulo = :titulo AND t.mensaje = :mensaje AND t.eliminado = false
	""")
	boolean findByTituloAndMensaje(@NotBlank String titulo, @NotNull String mensaje);

	Page<Topico> findByEliminadoFalse(Pageable paginacion);

	@Query("SELECT t FROM Topico t WHERE t.id = :id AND t.eliminado = false")
	Optional<Topico> findByIdAndEliminadoFalse(@NotNull Long id);

	boolean existsByIdAndEliminadoFalse(@NotNull Long aLong);
}
