package foro.hub.api.infra.errores;

import foro.hub.api.domain.ValidacionException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

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
