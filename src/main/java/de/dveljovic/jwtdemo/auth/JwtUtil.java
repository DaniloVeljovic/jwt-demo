package de.dveljovic.jwtdemo.auth;

import de.dveljovic.jwtdemo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "mysecretkey";
    private long ACCESS_TOKEN_VALIDITY = 60 * 60 * 1000;
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    private final JwtParser jwtParser;

    public JwtUtil() {
        jwtParser = Jwts.parser().setSigningKey(SECRET_KEY);
    }

    public String createToken(final User user) {
        final Claims claims = Jwts.claims().setSubject(user.email());
        claims.put("firstName", user.firstName());
        claims.put("lastName", user.lastName());

        final Date creationTime = new Date();
        final Date expirationTime = new Date(creationTime.getTime() + TimeUnit.MINUTES.toMillis(ACCESS_TOKEN_VALIDITY));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private Claims parseJwtClaims(final String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    Optional<String> resolveToken(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(TOKEN_HEADER);
        if(StringUtils.hasLength(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return Optional.of(bearerToken.substring(TOKEN_PREFIX.length()));
        }
        return Optional.empty();
    }

    public Claims resolveClaims(final HttpServletRequest request) {
        try {
            final Optional<String> maybeToken = resolveToken(request);
            return maybeToken.map(this::parseJwtClaims).orElse(null);
        } catch (ExpiredJwtException exception) {
            request.setAttribute("expired", exception.getMessage());
            throw exception;
        } catch (Exception ex) {
            request.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public boolean validateClaims(final Claims claims) {
        return claims.getExpiration().after(new Date());
    }

    public String getEmail(final Claims claims) {
        return claims.getSubject();
    }

    private List<String> getRoles(final Claims claims) {
        return (List<String>) claims.get("roles");
    }
}
