package foro.hub.api.controller;

import foro.hub.api.domain.ValidacionException;
import foro.hub.api.domain.respuesta.*;
import foro.hub.api.domain.topico.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/respuestas")
@SecurityRequirement(name = "bearer-key")
public class RespuestaController {

	@Autowired
	private RespuestaRepository respuestaRepository;

	@Autowired
	private LogicaRespuesta logicaRespuesta;

	@PostMapping
	@Transactional
	public ResponseEntity<DatosResRespuesta> registrarRespuesta(@RequestBody @Valid DatosRegistroRespuesta datosRegistroRespuesta,
	                                                            UriComponentsBuilder uriComponentsBuilder) {
		var respuestaRegistro = logicaRespuesta.registrar(datosRegistroRespuesta);

		URI uri = uriComponentsBuilder.path("/respuestas/{id}").buildAndExpand(respuestaRegistro.id()).toUri();
		return ResponseEntity.created(uri).body(respuestaRegistro);
	}

	@GetMapping
	@Parameters({
			@Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
			@Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
			@Parameter(name = "sort", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "fechaCreacion,desc"))
	})
	public ResponseEntity<Page<DatosListadoRespuesta>> getRespuestas(@PageableDefault(size = 10, sort = "fechaCreacion",
			direction = Sort.Direction.DESC) Pageable paginacion) {
		return ResponseEntity.ok(respuestaRepository.findByEliminadoFalse(paginacion).map(DatosListadoRespuesta::new));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DatosListadoRespuesta> getRespuesta(@PathVariable @Valid Long id) {
		var respuesta = respuestaRepository.findByIdAndEliminadoFalse(id).orElseThrow();
		return ResponseEntity.ok(new DatosListadoRespuesta(respuesta));
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<DatosResRespuesta> actualizarRespuesta(@PathVariable @Valid Long id,
	                                                       @RequestBody @Valid DatosActualizarRespuesta
			                                                       datosActualizarRespuesta) {
		var respuesta = logicaRespuesta.actualizar(id, datosActualizarRespuesta);
		return ResponseEntity.ok(respuesta);
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity eliminarRespuesta(@PathVariable @Valid Long id) {
		logicaRespuesta.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
