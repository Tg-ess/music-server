package org.example.musicserver.controller;

import org.example.musicserver.mapper.UserMapper;
import org.example.musicserver.model.User;
import org.example.musicserver.utils.Constant;
import org.example.musicserver.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RequestMapping("/user")
@RestController
public class UserController {

    // 自动装配
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // 用户登录
    @RequestMapping("/login")
    public Result<User> login(String username, String password, HttpSession session) {
        // 调用 mapper 层查询用户信息
        User user = userMapper.queryByUsername(username);
        if(user == null) {
            // 未查到
            System.out.println("登录失败");
            return new Result<>(-1,"用户名或密码错误", null);
        } else {
            // 查到
            if(bCryptPasswordEncoder.matches(password, user.getPassword())) {
                // 设置 session
                session.setAttribute(Constant.USER_SESSION_KEY, user);
                System.out.println("登录成功");
                user.setPassword("");
                return new Result<>(0, "登录成功", user);
            } else {
                user.setPassword("");
                return new Result<>(-1, "用户名或密码错误", user);
            }
        }
    }
}
