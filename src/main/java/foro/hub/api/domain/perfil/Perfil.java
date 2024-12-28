package foro.hub.api.domain.perfil;

import foro.hub.api.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

@Table(name = "perfiles")
@Entity(name = "Perfil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Perfil {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	private boolean eliminado;

	public Perfil(DatosRegistroPerfil datosRegistroPerfil, Usuario usuario) {
		this.nombre = datosRegistroPerfil.nombre();
		this.usuario = usuario;
		this.eliminado = false;
	}

	public void actualizar(@Valid DatosActualizarPerfil datosActualizarPerfil) {
		if (datosActualizarPerfil.nombre() != null) {
			this.nombre = datosActualizarPerfil.nombre();
		}
	}

	public void eliminar() {
		this.eliminado = true;
	}
}
