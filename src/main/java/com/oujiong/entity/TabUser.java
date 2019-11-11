package com.oujiong.entity;

import com.oujiong.plugin.AutoId;
import lombok.Data;

import java.util.Date;

/**
 * user表
 */
@Data
public class TabUser {
    /**
     * id(添加自定义主键ID)
     */
    @AutoId
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 是否删除 1删除 0未删除
     */
    private Integer status;

}