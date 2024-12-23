package foro.hub.api.domain;

public class UsuarioInvalido extends RuntimeException {
	public UsuarioInvalido(String message) {
		super(message);
	}
}
