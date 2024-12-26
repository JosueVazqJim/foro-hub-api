package foro.hub.api.controller;

import foro.hub.api.domain.ValidacionException;
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
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private LogicaTopico logicaTopico;

	@PostMapping
	@Transactional
	public ResponseEntity<DatosResTopico> registrarTopico(@RequestBody @Valid DatosRegistroTopico datosRegistroTopico,
	                                                      UriComponentsBuilder uriComponentsBuilder) {
		var respuestaRegistro = logicaTopico.registrar(datosRegistroTopico);

		URI uri = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(respuestaRegistro.id()).toUri();
		//URI uri = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(0).toUri();
		return ResponseEntity.created(uri).body(respuestaRegistro);
	}

	@GetMapping
	@Parameters({
			@Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
			@Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
			@Parameter(name = "sort", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "fechaCreacion,desc"))
	})
	public ResponseEntity<Page<DatosListadoTopicos>> getTopicos(@PageableDefault(size = 10, sort = "fechaCreacion",
			direction = Sort.Direction.DESC) Pageable paginacion) {
		return ResponseEntity.ok(topicoRepository.findByEliminadoFalse(paginacion).map(DatosListadoTopicos::new));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DatosListadoTopicos> getTopico(@PathVariable @Valid Long id) {
		var topico = topicoRepository.findById(id).orElseThrow();
		return ResponseEntity.ok(new DatosListadoTopicos(topico));
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<DatosResTopico> actualizarTopico(@PathVariable @Valid Long id,
	                                                       @RequestBody @Valid DatosActualizarTopico
			                                                       datosActualizarTopico) {
		var respuesta = logicaTopico.actualizar(id, datosActualizarTopico);
		return ResponseEntity.ok(respuesta);
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity eliminarTopico(@PathVariable @Valid Long id) {
		//borrado logico
//		logicaTopico.eliminar(id);

		//borrado fisico
		var optionalTopico = topicoRepository.findById(id);
		if (optionalTopico.isPresent()) {
			topicoRepository.delete(optionalTopico.get());
		} else {
			throw new ValidacionException("No existe un tópico con el id indicado");
		}
		return ResponseEntity.noContent().build();
	}
}
