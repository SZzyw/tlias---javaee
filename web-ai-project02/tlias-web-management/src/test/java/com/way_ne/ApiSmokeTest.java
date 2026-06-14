package com.way_ne;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSmokeTest {
    private static final String ALL_PERMISSIONS = String.join(",",
            "dashboard:view",
            "clazz:view",
            "clazz:edit",
            "student:view",
            "student:edit",
            "student:violation",
            "dept:view",
            "dept:edit",
            "emp:view",
            "emp:edit",
            "role:view",
            "role:edit",
            "report:emp",
            "report:stu",
            "log:view"
    );

    @Autowired
    private MockMvc mockMvc;

    @Test
    void captchaShouldReturnImagePayload() throws Exception {
        mockMvc.perform(get("/captcha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.captchaId").exists())
                .andExpect(jsonPath("$.data.imageBase64").exists());
    }

    @Test
    void loginShouldRejectInvalidCaptcha() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "123456",
                                  "captchaId": "invalid",
                                  "captchaCode": "0000"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("验证码错误或已过期"));
    }

    @Test
    void reportDashboardShouldBeProtected() throws Exception {
        mockMvc.perform(get("/report/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void securedReadEndpointsShouldRespondWithAdminToken() throws Exception {
        String token = createAdminToken();

        mockMvc.perform(get("/depts").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(get("/emps/list").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(get("/clazzs/list").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(get("/students")
                        .header("token", token)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(get("/roles").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(get("/permissions").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(get("/report/dashboard").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        mockMvc.perform(get("/log").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void studentCreateShouldRejectInvalidIdCard() throws Exception {
        String token = createAdminToken();

        mockMvc.perform(post("/students")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "测试学员",
                                  "no": "S90001",
                                  "gender": 1,
                                  "phone": "13812345678",
                                  "idCard": "110105194912310021",
                                  "isCollege": 1,
                                  "address": "上海市",
                                  "degree": 4,
                                  "graduationDate": "2024-06-30",
                                  "clazzId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("身份证号格式不正确"));
    }

    @Test
    void exportEndpointsShouldReturnExcelContent() throws Exception {
        String token = createAdminToken();

        mockMvc.perform(get("/report/exportEmp").header("token", token))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        mockMvc.perform(get("/report/exportStudent").header("token", token))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    private String createAdminToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1);
        claims.put("username", "api-smoke-admin");
        claims.put("permissions", ALL_PERMISSIONS);
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, "way_ne")
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .compact();
    }
}
