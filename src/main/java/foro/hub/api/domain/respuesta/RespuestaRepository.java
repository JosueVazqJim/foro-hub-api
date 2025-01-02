package foro.hub.api.domain.respuesta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
	@Query(""" 
		SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
		FROM Respuesta r
		WHERE r.id = :id AND r.mensaje = :mensaje AND r.eliminado = false
	""")
	boolean findByIdAndMensaje(@NotBlank Long id, @NotNull String mensaje);

	Page<Respuesta> findByEliminadoFalse(Pageable paginacion);

	@Query("SELECT r FROM Respuesta r WHERE r.id = :id AND r.eliminado = false")
	Optional<Respuesta> findByIdAndEliminadoFalse(@NotNull Long id);

	boolean existsByMensajeAndUsuarioIdAndTopicoIdAndEliminadoFalse(String mensaje, Long usuarioId, Long topicoId);
}