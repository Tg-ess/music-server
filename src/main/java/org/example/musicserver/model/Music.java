package org.example.musicserver.model;

import lombok.Data;
// 音乐类
@Data
public class Music {
    private Integer id; // 音乐id
    private String title; // 音乐名称
    private String singer; // 歌手
    private String time; // 上传时间
    private String url; // 音乐 url
    private Integer userId; // 用户id
}
