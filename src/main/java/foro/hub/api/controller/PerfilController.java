package foro.hub.api.controller;

import foro.hub.api.domain.PerfilInvalido;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/perfiles")
@SecurityRequirement(name = "bearer-key")
public class PerfilController {

	@Autowired
	private PerfilRepository perfilRepository;

	@Autowired
	private LogicaPerfil logicaPerfil;

	@PostMapping
	@Transactional
	public ResponseEntity<DatosResPerfil> registrarPerfil(@RequestBody @Valid DatosRegistroPerfil datosRegistroPerfil,
	                                                      UriComponentsBuilder uriComponentsBuilder) {
		var respuesta = logicaPerfil.registrar(datosRegistroPerfil);
		URI uri = uriComponentsBuilder.path("/perfiles/{id}").buildAndExpand(respuesta.id()).toUri();
		return ResponseEntity.created(uri).body(respuesta);
	}

	@GetMapping
	@Parameters({
			@Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
			@Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
			@Parameter(name = "sort", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "nombre"))
	})
	public ResponseEntity<Page<DatosListadoPerfiles>> getPerfiles(@PageableDefault(size = 10) Pageable paginacion) {
		return ResponseEntity.ok(perfilRepository.findByEliminadoFalse(paginacion).map(DatosListadoPerfiles::new));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DatosListadoPerfiles> getPerfil(@PathVariable @Valid Long id) {
		Optional<Perfil> perfil = perfilRepository.findByIdAndEliminadoFalse(id);
		if (perfil.isEmpty()) {
			throw new PerfilInvalido("No existe un Perfil con el id indicado");
		}
		return ResponseEntity.ok(new DatosListadoPerfiles(perfil.get()));
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<DatosResPerfil> actualizarPerfil(@PathVariable @Valid Long id,
	                                                       @RequestBody @Valid DatosActualizarPerfil datosActualizarPerfil) {
		var respuesta = logicaPerfil.actualizar(id, datosActualizarPerfil);
		return ResponseEntity.ok(respuesta);
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity eliminarPerfil(@PathVariable @Valid Long id) {
		logicaPerfil.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
