package com.way_ne;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    /**
     * 生成JWT令牌
     */
    @Test
    public void testGenerateJwt() {
        Map<String, Object> dataMap=new HashMap<>();
        dataMap.put("id",1);
        dataMap.put("username","wayne");
        String jwt=Jwts.builder().signWith(SignatureAlgorithm.HS256, "secret")
                .addClaims(dataMap)//添加自定义数据
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))//设置过期时间
                .compact();//生成令牌
        System.out.println(jwt);
    }
    /**
     * 解析JWT令牌
     */
    @Test
    public void testParseJwt() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ3YXluZSIsImV4cCI6MTc2NTU0NDI0Nn0.sqqTDnFxXr0_LJ8t6-zu8-a5a8ZzHAhetuJ6yOoHyXM";
        Claims claims =Jwts.parser().setSigningKey("secret").parseClaimsJws(token).getBody();
        System.out.println(claims);
    }
}
