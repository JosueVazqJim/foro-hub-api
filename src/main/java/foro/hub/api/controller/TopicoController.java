package foro.hub.api.controller;

import foro.hub.api.domain.topico.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

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
}
