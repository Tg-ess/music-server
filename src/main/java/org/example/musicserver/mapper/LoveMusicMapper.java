package org.example.musicserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.musicserver.model.Music;

import java.util.List;

@Mapper
public interface LoveMusicMapper {
    // 收藏音乐
    int insert(Integer musicId, Integer userId);

    // 取消收藏
    int delete(Integer musicId, Integer userId);

    // 根据用户id 和 音乐id查询收藏的音乐
    Music queryByUserIdAndMusicId(Integer userId, Integer musicId);

    // 根据名称查询用户喜欢的音乐
    List<Music> queryByNameAndUserId(String name, Integer userId);

    // 查询用户收藏的音乐
    List<Music> queryByUserId(Integer userId);
}
