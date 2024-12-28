package foro.hub.api.domain.perfil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroPerfil(@NotBlank String nombre, @NotNull Long idUsuario) {
}
