package org.example.musicserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 在 SpringBoot 中，默认 Spring Security 是生效的，此时接口都是被保护的
// 我们需要通过验证才能正常的访问。此时通过配置 exclude ，即可禁止默认的登录验证
@SpringBootApplication(exclude =
        {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class MusicServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicServerApplication.class, args);
    }

}
