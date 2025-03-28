package org.example.musicserver;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {

    public static void main(String[] args) {
        // 模拟从前端获取的密码
        String password = "123456";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String newPassword = bCryptPasswordEncoder.encode(password);
        System.out.println("加密的密码为：" + newPassword);
        // 每次加密得到的密码都不一样，但校验结果始终一致
        // 使用 matches 方法进行密码的校验
        boolean same_password_result = bCryptPasswordEncoder.matches(password, newPassword);
        // true
        System.out.println("加密的密码和正确的密码校验结果：" + same_password_result);
        boolean other_password_result = bCryptPasswordEncoder.matches("1234567", newPassword);
        // false
        System.out.println("加密的密码和错误的密码校验结果：" + other_password_result);
    }
}
