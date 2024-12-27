package utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "0130e0ab9b6a40f4286c94a44904ac1bf1b9cd610a1b87d6ba4bccc44f6565064951660c33b96d63c1cea421e538cc3b7ff1549067ae8a7166b399afdf9a58f9bc2b38b1783abd9cf0c2a3eb03d115815558e896249768a0ef16db3fb36f2c0508ac964f370eb73a911f4191b1ae802770839553c48d3348a8ad32d02d74c9877cf1553b05f0db6666af9af61abb1df72c96cc79a8184ac665682beaf87c6c30af99b6daf6094db735d37353101c8793333cfe2fa995e426ba5f90be112d36c3a0acb5eaaeafbfaff5c1c0153bdd87a4d8e7b17cc301a9bac9983e53ba2759fc68c86446ec2f3497016f2bf60c7888c42fea388ee878e8b773e2004e5ed91222";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    private static final long EXPIRATION_TIME = 3600000L;

    public static String generateToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String validateToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

}
