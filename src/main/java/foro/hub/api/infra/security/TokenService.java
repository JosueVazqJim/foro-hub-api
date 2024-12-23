package foro.hub.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import foro.hub.api.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TokenService {

	@Value("${api.security.secret}")
	private String apiSecret;

	public String generarToken(Usuario usuario) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(apiSecret);
			return JWT.create()
					.withIssuer("foro hub")
					.withSubject(usuario.getNombre())
					.withClaim("id", usuario.getId())
					.withExpiresAt(generarFechaExpiracion())
					.sign(algorithm);
		} catch (JWTCreationException exception){
			throw new RuntimeException("Error al generar token");
		}
	}

	public String getSubject(String token) { //obtener el usuario del token
		if (token == null) {
			throw new RuntimeException("Token vacio");
		}
		DecodedJWT verifier = null;
		try {
			Algorithm algorithm = Algorithm.HMAC256(apiSecret);
			verifier = JWT.require(algorithm)
					.withIssuer("foro hub")
					.build()
					.verify(token);
			verifier.getSubject();
		} catch (JWTVerificationException exception) {
			throw new RuntimeException("Token no valido");
		}

		if (verifier.getSubject() == null || verifier.getSubject().isEmpty()) {
			throw new RuntimeException("Token invalido dado que el subject esta vacio");
		}
		return verifier.getSubject();
	}

	private Instant generarFechaExpiracion() {
		return LocalDateTime.now().plusMinutes(5).atZone(ZoneId.of("America/Mexico_City")).toInstant();
	}
}