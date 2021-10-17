package com.liu.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;

@SpringBootTest
public class SpringBootSecurityApplicationTests {

    @Test
    void contextLoads() {

        //对原始密码加密
        String hashpw = BCrypt.hashpw("secret",BCrypt.gensalt()); System.out.println(hashpw); //校验原始密码和BCrypt密码是否一致
        boolean checkpw = BCrypt.checkpw("123", "$2a$10$NlBC84MVb7F95EXYTXwLneXgCca6/GipyWR5NHm8K0203bSQMLpvm");
        System.out.println(checkpw);
    }

}
