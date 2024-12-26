package foro.hub.api.controller;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.usuario.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
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

	@GetMapping
	@Parameters({
			@Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
			@Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
			@Parameter(name = "sort", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "nombre"))
	})
	public ResponseEntity<Page<DatosListadoUsuarios>> getUsuarios(@PageableDefault(size = 10) Pageable paginacion) {
		return ResponseEntity.ok(usuarioRepository.findAll(paginacion).map(DatosListadoUsuarios::new));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DatosListadoUsuarios> getUsuario(@PathVariable @Valid Long id) {
		var usuario = usuarioRepository.findById(id).orElseThrow();
		return ResponseEntity.ok(new DatosListadoUsuarios(usuario));
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<DatosResUsuario> actualizarUsuario(@PathVariable @Valid Long id,
	                                                       @RequestBody @Valid DatosActualizarUsuario
			                                                       datosActualizarUsuario) {
		var respuesta = logicaUsuario.actualizar(id, datosActualizarUsuario);
		return ResponseEntity.ok(respuesta);
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity eliminarUsuario(@PathVariable @Valid Long id) {
		logicaUsuario.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
