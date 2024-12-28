package foro.hub.api.domain.usuario;

import jakarta.validation.constraints.Email;

public record DatosActualizarUsuario(String nombre, @Email String email,
                                     String contrasena) {
}
