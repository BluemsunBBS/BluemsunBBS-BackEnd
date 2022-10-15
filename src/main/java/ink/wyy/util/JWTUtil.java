package ink.wyy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTUtil {

    private static String secret = "BluemsunBBS";

    public static String createToken(Object subject) {
        return Jwts.builder()
                .setSubject(JSONUtil.toJSONString(subject))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public static <T> T parseToken(String token, Class<T> clazz)
    {
        Claims body = null;
        try {
            body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return null;
        }
        return JSONUtil.parseObject(body.getSubject(), clazz);
    }
}
