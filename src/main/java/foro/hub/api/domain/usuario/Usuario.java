package foro.hub.api.domain.usuario;

import foro.hub.api.domain.perfil.Perfil;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Usuario implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private String email;
	private String contrasena;
	private boolean eliminado;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Perfil> perfiles = new ArrayList<>();

	public Usuario(DatosRegistroUsuario datosRegistroUsuario, String contrasena) {
		this.nombre = datosRegistroUsuario.nombre();
		this.email = datosRegistroUsuario.email();
		this.contrasena = contrasena;
		this.eliminado = false;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER")); //sera el rol por defecto de los usuarios
	}

	@Override
	public String getPassword() {
		return contrasena;
	}

	@Override
	public String getUsername() {
		return nombre;
	}

	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}

	public void actualizar(@Valid DatosActualizarUsuario datosActualizarUsuario) {
		if (datosActualizarUsuario.nombre() != null) {
			this.nombre = datosActualizarUsuario.nombre();
		}
		if (datosActualizarUsuario.email() != null) {
			this.email = datosActualizarUsuario.email();
		}
		if (datosActualizarUsuario.contrasena() != null) {
			this.contrasena = datosActualizarUsuario.contrasena();
		}
	}

	public void actualizar(@Valid DatosActualizarUsuario datosActualizarUsuario, String contrasena) {
		if (datosActualizarUsuario.nombre() != null) {
			this.nombre = datosActualizarUsuario.nombre();
		}
		if (datosActualizarUsuario.email() != null) {
			this.email = datosActualizarUsuario.email();
		}
		if (contrasena != null || !contrasena.isEmpty() || !contrasena.isBlank()) {
			this.contrasena = contrasena;
		}
	}

	public void eliminar() {
		this.eliminado = true;
	}

	public void addPerfil(Perfil perfil) {
		perfiles.add(perfil);
		perfil.setUsuario(this);
	}

	public void removePerfil(Perfil perfil) {
		perfil.setEliminado(true);
	}
}
