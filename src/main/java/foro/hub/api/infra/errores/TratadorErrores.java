package foro.hub.api.infra.errores;

import foro.hub.api.domain.ValidacionException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice // un restControllerAdvice es un componente de spring que permite
// interceptar las llamadas a los controladores
public class TratadorErrores {

	//cuando se lance una excepcion de tipo ValidacionException, se ejecutara este metodo
	@ExceptionHandler(ValidacionException.class)
	public ResponseEntity tratarErrorValidacion(ValidacionException e) {
		return ResponseEntity.badRequest().body(new DatosError(e.getMessage()));
	}

	//cuando el cuerpo de la req no es valido, se ejecutara este metodo
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity tratarError400(MethodArgumentNotValidException e) {
		var errores = e.getFieldErrors().stream().map(DatosErrorValidacion::new).toList();
		return ResponseEntity.badRequest().body(errores);
	}

	//tratar error de elemento no encontrado
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity elementoNoEncontrado() {
		return ResponseEntity.notFound().build();
	}

	//recurso no encontrado
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity recursoNoEncontrado() {
		return ResponseEntity.notFound().build();
	}

	private record DatosErrorValidacion(String campo, String error) {
		public DatosErrorValidacion(FieldError error){
			this(error.getField(), error.getDefaultMessage());
		}
	}

	private record DatosError(String error) {
		public DatosError(FieldError error){
			this(error.getDefaultMessage());
		}
	}
}
