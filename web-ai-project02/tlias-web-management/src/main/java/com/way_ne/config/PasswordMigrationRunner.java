package com.way_ne.config;

import com.way_ne.mapper.EmpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<Map<String, Object>> users = empMapper.selectAllPasswords();
        for (Map<String, Object> user : users) {
            String password = (String) user.get("password");
            if (password != null && !password.startsWith("$2a$")) {
                Number idNum = (Number) user.get("id");
                empMapper.updatePassword(idNum.intValue(), passwordEncoder.encode(password));
            }
        }
    }
}
