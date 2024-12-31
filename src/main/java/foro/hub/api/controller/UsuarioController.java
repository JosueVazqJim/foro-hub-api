package foro.hub.api.controller;

import foro.hub.api.domain.PerfilInvalido;
import foro.hub.api.domain.UsuarioInvalido;
import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.perfil.*;
import foro.hub.api.domain.usuario.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PerfilRepository perfilRepository;

	@Autowired
	private LogicaUsuario logicaUsuario;

	@PostMapping
	@Transactional
	public ResponseEntity<DatosResUsuario> registrarUsuario(@RequestBody @Valid DatosRegistroUsuario datosRegistroUsuario,
	                                                        UriComponentsBuilder uriComponentsBuilder) {
		var respuesta = logicaUsuario.registrar(datosRegistroUsuario);
		URI uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(respuesta.id()).toUri();
		return ResponseEntity.created(uri).body(respuesta);
	}

	@SecurityRequirement(name = "bearer-key")
	@GetMapping
	@Parameters({
			@Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
			@Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
			@Parameter(name = "sort", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "nombre"))
	})
	public ResponseEntity<Page<DatosListadoUsuarios>> getUsuarios(@PageableDefault(size = 10) Pageable paginacion) {
		return ResponseEntity.ok(usuarioRepository.findByEliminadoFalse(paginacion).map(DatosListadoUsuarios::new));
	}

	@SecurityRequirement(name = "bearer-key")
	@GetMapping("/{id}")
	public ResponseEntity<DatosListadoUsuarios> getUsuario(@PathVariable @Valid Long id) {
		System.out.println("id: " + id);
		Optional<Usuario> usuario = usuarioRepository.findByIdAndEliminadoFalse(id);
		if (usuario.isEmpty()) {
			throw new UsuarioInvalido("No existe un Usuario con el id indicadosssssss");
		}
		return ResponseEntity.ok(new DatosListadoUsuarios(usuario.get()));
	}

	@SecurityRequirement(name = "bearer-key")
	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<DatosResUsuario> actualizarUsuario(@PathVariable @Valid Long id,
	                                                       @RequestBody @Valid DatosActualizarUsuario
			                                                       datosActualizarUsuario) {
		var respuesta = logicaUsuario.actualizar(id, datosActualizarUsuario);
		return ResponseEntity.ok(respuesta);
	}

	@SecurityRequirement(name = "bearer-key")
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity eliminarUsuario(@PathVariable @Valid Long id) {
		logicaUsuario.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{idUsuario}/perfiles")
	@SecurityRequirement(name = "bearer-key")
	public ResponseEntity<List<DatosListadoPerfiles>> getPerfiles(@PathVariable Long idUsuario) {
		Optional<Usuario> usuario = usuarioRepository.findByIdAndEliminadoFalse(idUsuario);
		if (usuario.isEmpty()) {
			throw new UsuarioInvalido("No existe un Usuario con el id indicado");
		}
		List<DatosListadoPerfiles> perfiles = usuario.get().getPerfiles().stream()
				.filter(p -> !p.isEliminado())
				.map(DatosListadoPerfiles::new)
				.toList();
		return ResponseEntity.ok(perfiles);
	}

	@GetMapping("/{idUsuario}/perfiles/{idPerfil}")
	@SecurityRequirement(name = "bearer-key")
	public ResponseEntity<DatosListadoPerfiles> GetSingularPerfil(@PathVariable Long idUsuario, @PathVariable Long idPerfil) {
		Optional<Usuario> usuario = usuarioRepository.findByIdAndEliminadoFalse(idUsuario);
		if (usuario.isEmpty()) {
			throw new UsuarioInvalido("No existe un Usuario con el id indicado");
		}
		DatosListadoPerfiles perfil = usuario.get().getPerfiles().stream()
				.filter(p -> p.getId().equals(idPerfil) && !p.isEliminado())
				.findFirst()
				.map(DatosListadoPerfiles::new)
				.orElseThrow(() -> new PerfilInvalido("No existe un Perfil con el id indicado"));
		return ResponseEntity.ok(perfil);
	}

	@PostMapping("/{idUsuario}/perfiles")
	@Transactional
	@SecurityRequirement(name = "bearer-key")
	public ResponseEntity<DatosResPerfil> registrarPerfil(@PathVariable Long idUsuario, @RequestBody @Valid DatosRegistroUsuarioPerfil datos, UriComponentsBuilder uriComponentsBuilder) {
		Optional<Usuario> usuario = usuarioRepository.findByIdAndEliminadoFalse(idUsuario);
		if (usuario.isEmpty()) {
			throw new UsuarioInvalido("No existe un Usuario con el id indicado");
		}
		//perfiles duplicados
		boolean duplicado = perfilRepository.findByNombreAndIdUsuario(datos.nombre(), idUsuario);
		if (duplicado) {
			throw new ValidacionException("El usuario ya tiene un Perfil con el mismo nombre");
		}
		Perfil perfil = new Perfil(datos, usuario.get());
		usuario.get().addPerfil(perfil);
		usuarioRepository.save(usuario.get()); // This will cascade and save the perfil as well
		DatosResPerfil respuesta = new DatosResPerfil(perfil.getId(), perfil.getNombre(), idUsuario);
		URI uri = uriComponentsBuilder.path("/usuarios/{idUsuario}/perfiles/{idPerfil}").buildAndExpand(idUsuario, perfil.getId()).toUri();
		return ResponseEntity.created(uri).body(respuesta);
	}


	@PutMapping("/{idUsuario}/perfiles/{idPerfil}")
	@Transactional
	@SecurityRequirement(name = "bearer-key")
	public ResponseEntity<DatosResPerfil> actualizarPerfil(@PathVariable Long idUsuario, @PathVariable Long idPerfil,
	                                                       @RequestBody @Valid DatosActualizarPerfil datosActualizarPerfil) {
		Optional<Usuario> usuario = usuarioRepository.findByIdAndEliminadoFalse(idUsuario);
		if (usuario.isEmpty()) {
			throw new UsuarioInvalido("No existe un Usuario con el id indicado");
		}
		Perfil perfil = usuario.get().getPerfiles().stream()
				.filter(p -> p.getId().equals(idPerfil))
				.findFirst()
				.orElseThrow(() -> new PerfilInvalido("No existe un Perfil con el id indicado"));
		perfil.actualizar(datosActualizarPerfil);
		usuarioRepository.save(usuario.get());
		return ResponseEntity.ok(new DatosResPerfil(perfil.getId(), perfil.getNombre(), idUsuario));
	}

	@DeleteMapping("/{idUsuario}/perfiles/{idPerfil}")
	@Transactional
	@SecurityRequirement(name = "bearer-key")
	public ResponseEntity eliminarPerfil(@PathVariable Long idUsuario, @PathVariable Long idPerfil) {
		Optional<Usuario> usuario = usuarioRepository.findByIdAndEliminadoFalse(idUsuario);
		if (usuario.isEmpty()) {
			throw new UsuarioInvalido("No existe un Usuario con el id indicado");
		}
		Perfil perfil = usuario.get().getPerfiles().stream()
				.filter(p -> p.getId().equals(idPerfil) && !p.isEliminado())
				.findFirst()
				.orElseThrow(() -> new UsuarioInvalido("No existe un Perfil con el id indicado"));
		usuario.get().removePerfil(perfil);
		usuarioRepository.save(usuario.get());
		return ResponseEntity.noContent().build();
	}
}
