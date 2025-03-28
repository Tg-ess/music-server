package org.example.musicserver.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    // 定义一个固定的盐值
    private static final String salt = "1a2b3c4d";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    /**
     * 第一次加密：模拟前端自己加密，然后传到后端
     * @param inputPass
     * @return
     */
    public static String inputPassToFromPass(String inputPass) {
        // 密码和盐值中的部分字符拼接字符串
        String str = "" + salt.charAt(1) + salt.charAt(3) + inputPass + salt.charAt(5) + salt.charAt(6);
        return md5(str);
    }

    /**
     * 第二次加密
     * @param fromPass 前端加密过的密码，传给后端进行第二次加密
     * @param salt     用户数据库当中的盐值
     * @return
     */
    public static String fromPassToDBPass(String fromPass, String salt) {
        // 把加密过的密码和盐值中的部分字符拼接字符串
        String str = "" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(4) + salt.charAt(5);
        return md5(str);
    }

    /**
     * 上面两个方法合到一起使用
     * @param inputPass
     * @param saltDB
     * @return
     */
    public static String inputPassToDBPass(String inputPass, String saltDB) {
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass, saltDB);
        return dbPass;
    }

    public static void main(String[] args) {

        System.out.println("对用户输入密码进行第一次加密：" + inputPassToFromPass("123456"));
        // 不管运行多少次 这两个密码结果都是一样的
        System.out.println("对用户输入密码进行第二次加密：" + fromPassToDBPass(inputPassToFromPass("123456"), "1a2b3c4d"));
        System.out.println("对用户输入密码进行两次加密：" + inputPassToDBPass("123456", "1a2b3c4d"));

//        对用户输入密码进行第一次加密：d13317cf4dd81b736ff9498ab62f1f1d
//        对用户输入密码进行第二次加密：c66d9e19468852e23316cac8008dcaa7
//        对用户输入密码进行两次加密：c66d9e19468852e23316cac8008dcaa7
    }
















}
