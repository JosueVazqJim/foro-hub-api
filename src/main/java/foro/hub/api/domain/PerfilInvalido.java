package foro.hub.api.domain;

public class PerfilInvalido extends RuntimeException {
	public PerfilInvalido(String message) {
		super(message);
	}
}
