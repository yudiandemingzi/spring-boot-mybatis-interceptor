package com.jincou.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xub
 * @Description: 用户VO（QO、VO、DTO都用这个）
 * @date 2019/8/7 下午9:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserVO {
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

}