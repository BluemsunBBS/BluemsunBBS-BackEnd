package ink.wyy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class JWTUtil {

    private static String secret = "BluemsunBBS";

    public static String createToken(Object subject) {
        try {
            return Jwts.builder()
                    .setSubject(URLEncoder.encode(JSONUtil.toJSONString(subject), "UTF-8"))
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T parseToken(String token, Class<T> clazz)
    {
        Claims body = null;
        try {
            body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return null;
        }
        try {
            return JSONUtil.parseObject(URLDecoder.decode(body.getSubject(), "UTF-8"), clazz);
        } catch (Exception e) {
            return null;
        }
    }
}
