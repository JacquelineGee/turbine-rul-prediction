package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
@Slf4j
public class JwtUtils {

    private static String signKey = "hualicat";
    private static Long expire = 43200000L;

    /**
     * 生成JWT令牌
     * @param claims JWT第二部分负载 payload 中存储的内容
     * @return
     */
    public static String generateJwt(Map<String, Object> claims){
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }
    /**
            * 从JWT中获取用户ID
 * @param jwt JWT令牌
 * @return 用户ID
 */
    public static String getUserId(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("id", String.class);
    }

/**
        * 从JWT中获取用户名
 * @param jwt JWT令牌
 * @return 用户名
 */
    public static String getUsername(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("username", String.class);
    }
    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt){
        if (!StringUtils.hasText(jwt)) {
            log.warn("尝试解析空JWT");
            throw new IllegalArgumentException("JWT令牌不能为空");
        }

        // 处理Bearer前缀
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7).trim();
        }

        if (jwt.isEmpty()) {
            log.warn("JWT令牌为空字符串");
            throw new IllegalArgumentException("JWT令牌不能为空");
        }

        try {
            return Jwts.parser()
                    .setSigningKey(signKey)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (MalformedJwtException e) {
            log.info("JWT格式错误: {}", jwt);
            throw new IllegalArgumentException("无效的令牌格式");
        } catch (Exception e) {
            log.error("JWT解析错误", e);
            throw new RuntimeException("令牌解析失败");
        }
    }
}
