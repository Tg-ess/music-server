package org.example.musicserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.musicserver.model.Music;

import java.util.List;

@Mapper
public interface MusicMapper {

    // 添加音乐
    int insertMusic(String title, String singer, String time, String url, Integer userId);

    // 通过名称和作者搜索音乐
    Music queryByTitleAndSinger(String title, String singer);

    // 通过 id 删除音乐
    int deleteMusicById(Integer id);

    // 通过 id 搜索音乐
    Music queryById(Integer id);

    // 通过名称搜索音乐（名称为空时，搜索全部音乐）
    List<Music> queryByTitle(String title, Integer userId);

    // 搜索音乐
    List<Music> query(Integer userId);
}
