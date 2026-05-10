package com.way_ne.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static final String SECRET_KEY = "way_ne";
    private static final long EXPIRATION_TIME = 12*60*60*1000;
    /**
     * 生成JWT令牌
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .addClaims(claims)//添加自定义数据
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))//设置过期时间
                .compact();//生成令牌
    }
    /**
     * 解析JWT令牌
     */
    public static Claims parseToken(String token)  {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
