package org.example.musicserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.musicserver.model.User;

@Mapper
public interface UserMapper {
    // 用户登录
    User login(User loginUser);
    // 通过用户名查询用户信息
    User queryByUsername(String username);
}
