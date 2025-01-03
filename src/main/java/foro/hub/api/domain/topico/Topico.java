package foro.hub.api.domain.topico;

import foro.hub.api.domain.curso.Curso;
import foro.hub.api.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "topicos")
@Entity(name = "Topico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String titulo;
	private String mensaje;
	@Column(name = "fecha_creacion")
	private LocalDateTime fechaCreacion;
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	private Curso curso;

	private boolean eliminado;

	public Topico(DatosRegistroTopico datosRegistroTopico, Usuario usuario, Curso curso) {
		this.titulo = datosRegistroTopico.titulo();
		this.mensaje = datosRegistroTopico.mensaje();
		this.fechaCreacion = LocalDateTime.now();
		this.status = "Activo";
		this.usuario = usuario;
		this.curso = curso;
		this.eliminado = false;
	}

	public void actualizar(@Valid DatosActualizarTopico datosActualizarTopico) {
		if (datosActualizarTopico.titulo() != null) {
			this.titulo = datosActualizarTopico.titulo();
		}
		if (datosActualizarTopico.mensaje() != null) {
			this.mensaje = datosActualizarTopico.mensaje();
		}
		if (datosActualizarTopico.idCurso() != null) {
			this.curso.setId(datosActualizarTopico.idCurso());
		}
		if (datosActualizarTopico.idUsuario() != null) {
			this.usuario.setId(datosActualizarTopico.idUsuario());
		}
	}
}