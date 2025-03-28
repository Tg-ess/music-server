package org.example.musicserver.model;

import lombok.Data;
// 用户类
@Data
public class User {
    private Integer id; // 用户id
    private String username; // 用户名
    private String password; // 密码
}
