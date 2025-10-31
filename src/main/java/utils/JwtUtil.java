package utils;

import com.example.FileShareAPI.Back_End.exception.UnAuthorizedException;
import com.example.FileShareAPI.Back_End.model.Role;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseCookie;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = Dotenv.load().get("JWT_SECRET");
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    private static final long EXPIRATION_TIME = Long.parseLong(Dotenv.load().get("JWT_EXPIRATION_TIME"));

    /**
     *
     * @param userId - id of user
     * @return JWT token
     */
    public static ResponseCookie generateCookie(String userId, Role role) {
        String token = generateToken(userId, role);
        System.out.println("token: " + token);
        return ResponseCookie.from("ACCESS_TOKEN", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(EXPIRATION_TIME / 1000)
                .build();
    }

    /**
     *
     * @param userId - id of user
     * @return JWT token
     */
    public static String generateToken(String userId, Role role) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     *
     * @param token -
     * @return user's UUID
     */
    public static Jws<Claims> validate(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            throw new UnAuthorizedException("Invalid or Expired token");
        }
    }

    public static ResponseCookie generateLogoutCookie() {
        return ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }
}