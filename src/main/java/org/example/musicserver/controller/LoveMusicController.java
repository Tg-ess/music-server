package org.example.musicserver.controller;

import org.example.musicserver.mapper.LoveMusicMapper;
import org.example.musicserver.model.Music;
import org.example.musicserver.model.User;
import org.example.musicserver.utils.Constant;
import org.example.musicserver.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/lovemusic")
public class LoveMusicController {

    @Autowired
    private LoveMusicMapper loveMusicMapper;

    // 收藏音乐
    @RequestMapping("/like")
    public Result<Boolean> like(Integer musicId, HttpSession session) {
        // 检查登录状态
        if(session.getAttribute(Constant.USER_SESSION_KEY) == null) {
            System.out.println("未登录");
            return new Result<>(-1, "请登录后操作", false);
        }
        // 参数校验
        if(musicId == null) {
            System.out.println("参数为空 musicId: " + musicId);
            return new Result<>(-1, "参数为空", false);
        }
        // 先查询是否已经收藏过
        // 获取 session 中的用户
        User user = (User) session.getAttribute(Constant.USER_SESSION_KEY);
        Integer userId = user.getId();
        Music music = loveMusicMapper.queryByUserIdAndMusicId(userId, musicId);
        if(music != null) {
            System.out.println("收藏的音乐已经存在 music：" + music);
            return new Result<>(-1, "音乐已经收藏过了", false);
        }
        int i = loveMusicMapper.insert(musicId, userId);
        if(i != 1) {
            System.out.println("收藏失败");
            return new Result<>(-1, "收藏音乐失败", false);
        }
        return new Result<>(0 , "收藏成功", true);
    }

    // 取消收藏
    @RequestMapping("/cancel")
    public Result<Boolean> cancel(Integer musicId, HttpSession session) {
        // 检查登录状态
        if(session.getAttribute(Constant.USER_SESSION_KEY) == null) {
            System.out.println("未登录");
            return new Result<>(-1, "请登录后操作", false);
        }
        // 参数校验
        if(musicId == null) {
            System.out.println("参数为空 musicId: " + musicId);
            return new Result<>(-1, "参数为空", false);
        }
        User user = (User) session.getAttribute(Constant.USER_SESSION_KEY);
        Integer userId = user.getId();
        int i = loveMusicMapper.delete(musicId, userId);
        if(i != 1) {
            return new Result<>(-1, "取消收藏失败", false);
        }
        return new Result<>(0, "取消收藏成功", true);
    }

    // 搜索音乐 根据 名称
    @RequestMapping("/query")
    public Result<List<Music>> queryByName(String name, HttpSession session) {
        // 检查登录状态
        if(session.getAttribute(Constant.USER_SESSION_KEY) == null) {
            System.out.println("未登录");
            return new Result<>(-1, "请登录后操作", null);
        }
        User user = (User) session.getAttribute(Constant.USER_SESSION_KEY);
        Integer userId = user.getId();
        List<Music> musicList = null;
        if(!StringUtils.hasLength(name)) {
            musicList = loveMusicMapper.queryByUserId(userId);
        } else {
            musicList = loveMusicMapper.queryByNameAndUserId(name, userId);
        }
        return new Result<>(0, "搜索成功", musicList);
    }
}
