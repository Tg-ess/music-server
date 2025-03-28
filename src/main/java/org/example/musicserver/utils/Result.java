package org.example.musicserver.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一返回数据类型
 * @param <T>
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private Integer status;
    private String message;
    private T data;
}
