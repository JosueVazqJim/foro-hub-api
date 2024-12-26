package foro.hub.api.controller;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.topico.*;
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
import org.springframework.data.domain.Sort;
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
	private LogicaTopico logicaTopico;

	@PostMapping
	@Transactional
	public ResponseEntity<DatosResUsuario> registrarUsuario(@RequestBody @Valid DatosRegistroUsuario datosRegistroUsuario,
	                                                        UriComponentsBuilder uriComponentsBuilder) {
		var claveEncriptada = encriptarBCrypt(datosRegistroUsuario.contrasena());

		Usuario usuario = usuarioRepository.save(new Usuario(datosRegistroUsuario, claveEncriptada));

		DatosResUsuario datosResUsuario = new DatosResUsuario(usuario.getId(), usuario.getNombre(), usuario.getEmail());

		URI uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(datosResUsuario.id()).toUri();
		return ResponseEntity.created(uri).body(datosResUsuario);
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
		var usuario = usuarioRepository.findById(id).orElseThrow();
		if (datosActualizarUsuario.contrasena() != null) {
			usuario.actualizar(datosActualizarUsuario, encriptarBCrypt(datosActualizarUsuario.contrasena()));
		} else {
			usuario.actualizar(datosActualizarUsuario);
		}

		return ResponseEntity.ok(new DatosResUsuario(usuario.getId(), usuario.getNombre(), usuario.getEmail()));
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity eliminarUsuario(@PathVariable @Valid Long id) {

		var optionalUsuario = usuarioRepository.findById(id);
		if (optionalUsuario.isPresent()) {
			var usuario = optionalUsuario.get();
			usuario.eliminar();
			//usuarioRepository.delete(optionalUsuario.get());
		} else {
			throw new ValidacionException("No existe un tópico con el id indicado");
		}

		return ResponseEntity.noContent().build();
	}

	private String encriptarBCrypt(String clave) {
		var encoder = new BCryptPasswordEncoder();
		return encoder.encode(clave);
	}
}
