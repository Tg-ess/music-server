package org.example.musicserver.controller;

import org.example.musicserver.mapper.LoveMusicMapper;
import org.example.musicserver.mapper.MusicMapper;
import org.example.musicserver.model.Music;
import org.example.musicserver.model.User;
import org.example.musicserver.utils.Constant;
import org.example.musicserver.utils.Result;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Value("${music.local.path}")
    private String SAVE_PATH;

    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    private LoveMusicMapper loveMusicMapper;

    @RequestMapping("/upload")
    public Result<Boolean> uploadMusic(String singer, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws ReadOnlyFileException, IOException {
        // 查看session
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute(Constant.USER_SESSION_KEY) == null) {
            // session 为空
            System.out.println("未登录");
            return new Result<>(-1, "请登录后再上传", false);
        }

        // 参数校验
        if(!StringUtils.hasLength(singer) || file.isEmpty()) {
            return new Result<>(-1, "参数为空", false);
        }

        //判断音乐是否已经上传过 根据 title 和 singer
        int index = file.getOriginalFilename().lastIndexOf(".");
        String title = file.getOriginalFilename().substring(0, index);
        Music music = musicMapper.queryByTitleAndSinger(title, singer);
        if(music != null) {
            // 已经上传过了
            return new Result<>(-1, "上传音乐失败", false);
        }

        // 获取文件的名字  xxx.mp3
        String fileName = file.getOriginalFilename();
        System.out.println("fileName:" + fileName);

        // 整个路径
        String path = SAVE_PATH + fileName;
        System.out.println("path:" + path);

        File dest = new File(path);
        if(!dest.exists()) {
            dest.mkdirs();
        }
        // 上传文件到目标路径
        try {
            file.transferTo(dest);
            // 判断上传的文件是否为音乐
            MP3File mp3File = (MP3File) AudioFileIO.read(dest);
            // 这个 if 语句其实没什么用，本来是想通过 if 语句来判断到底是不是mp3文件
            // 但是后来发现它会抛异常，根本执行不到这里，所有就不用这种方式了
//            if(!mp3File.hasID3v1Tag() || !mp3File.hasID3v2Tag()) {
//                dest.delete();
//                return new Result<>(-1, "请上传音乐文件", false);
//            }
        } catch (IOException e) {
            if(dest != null) {
                dest.delete();
            }
            e.printStackTrace();
            return new Result<>(-1, "上传文件失败", false);
        } catch (InvalidAudioFrameException | CannotReadException | TagException e) {
            dest.delete();
            e.printStackTrace();
            return new Result<>(-1, "请上传音乐文件", false);
        }
        // 把音乐添加到数据库中
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = simpleDateFormat.format(new Date());
        String url = "/music/get?path=" + title;
        User user = (User) session.getAttribute(Constant.USER_SESSION_KEY);
        Integer userId = user.getId();
        int i = musicMapper.insertMusic(title, singer, time, url, userId);
        if(i == 1) {
            // 这里应该跳转到列表页
            response.sendRedirect("/list.html");
            return new Result<>(0, "数据库上传成功", true);
        } else {
            // 添加音乐失败，将上传到服务器的音乐一起删掉
            dest.delete();
            return new Result<>(-1, "数据库上传失败", false);
        }
    }

    // 播放音乐
    @RequestMapping("/get")
    public ResponseEntity<byte[]> get(String path) {
        if(!StringUtils.hasLength(path)) {
            return ResponseEntity.badRequest().build();
        }
        File file = new File(SAVE_PATH + path);
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(file.toPath());
            if(bytes == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    // 删除音乐
    @RequestMapping("/delete")
    public Result<Boolean> delete(Integer id, HttpSession session) {
        // 检查用户是否登录
        if(session.getAttribute(Constant.USER_SESSION_KEY) == null) {
            return new Result<>(-1, "用户未登录", false);
        }
        // 参数校验
        if(id == null || id <= 0) {
            return new Result<>(-1, "参数为空", false);
        }
        // 查询要删除的音乐
        Music music = musicMapper.queryById(id);
        // 找不到
        if(music == null) {
            return new Result<>(-1, "没有找到要删除的音乐", false);
        }
        // 删除数据库
        // 删除音乐之前需要先删除数据库中的收藏音乐
        User user = (User) session.getAttribute(Constant.USER_SESSION_KEY);
        Integer userId = user.getId();
        loveMusicMapper.delete(id, userId);
        int i = musicMapper.deleteMusicById(id);
        if(i != 1) {
            return new Result<>(-1, "删除音乐失败", false);
        }
        // 删除服务器中的音乐
        File file = new File(SAVE_PATH + music.getTitle() + ".mp3");
            if(file.delete()) {
                return new Result<>(0, "删除服务器数据成功", true);
            }
            return new Result<>(0, "删除服务器数据成功", true);
    }

    // 批量删除
    @RequestMapping("/deleteMany")
    public Result<Boolean> deleteMany(@RequestParam("ids[]") List<Integer> ids, HttpSession session) {
        // 检查用户是否登录
        if(session.getAttribute(Constant.USER_SESSION_KEY) == null) {
            return new Result<>(-1, "用户未登录", false);
        }
        // 参数校验
        if(ids == null || ids.isEmpty()) {
            return new Result<>(-1, "参数为空", false);
        }
        // 后续删除收藏音乐时会用到
        User user = (User) session.getAttribute(Constant.USER_SESSION_KEY);
        Integer userId = user.getId();

        // 循环删除
        for(Integer id : ids) {
            // 先查询
            Music music = musicMapper.queryById(id);
            //未查到
            if(music == null) {
                System.out.println("没有您要删除的音乐 id:" + id);
            } else {
                // 查到之后删除
                loveMusicMapper.delete(id, userId);
                int i = musicMapper.deleteMusicById(id);
                if(i != 1) {
                    return new Result<>(-1, "删除音乐失败", false);
                } else {
                    // 删除成功再删除服务器中的音乐
                    File file = new File(SAVE_PATH + music.getTitle() + ".mp3");
                    if(!file.delete()) {
                        return new Result<>(-1, "删除服务器中的音乐失败", false);
                    }
                }
            }
        }
        // 程序执行到这里，说明循环中没有出错，直接返回成功
        return new Result<>(0, "删除音乐成功", true);
    }

    // 查询音乐
    @RequestMapping("/query")
    public Result<List<Music>> queryMusicByTitle(String title, HttpSession session) {
        // 检查用户是否登录
        if(session.getAttribute(Constant.USER_SESSION_KEY) == null) {
            return new Result<>(-1, "用户未登录", null);
        }
        User user = (User) session.getAttribute(Constant.USER_SESSION_KEY);
        Integer userId = user.getId();
        List<Music> list = null;
        if(StringUtils.hasLength(title)) {
            list = musicMapper.queryByTitle(title, userId);
        } else {
            list = musicMapper.query(userId);
        }
        return new Result<>(0, "搜索音乐成功", list);
    }
}
