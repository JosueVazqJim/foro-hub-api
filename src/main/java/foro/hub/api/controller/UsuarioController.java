package foro.hub.api.controller;

import foro.hub.api.domain.UsuarioInvalido;
import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.perfil.DatosListadoPerfiles;
import foro.hub.api.domain.perfil.DatosResPerfil;
import foro.hub.api.domain.perfil.Perfil;
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
				.map(DatosListadoPerfiles::new)
				.toList();
		return ResponseEntity.ok(perfiles);
	}
}
