package com.jincou.plugin;

import java.lang.annotation.*;

/**
  * @Description: 主键注解
  * 支持两种主键：雪花ID 和 UUID
  *
  * @author xub
  * @date 2019/8/18 下午9:51
  */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoId {

    /**
     * @return id类型（默认为雪花id）
     */
    IdType value() default IdType.SNOWFLAKE;

    /**
     * id类型
     */
    enum IdType {
        /**
         * UUID去掉“-”
         */
        UUID,
        /**
         * 雪花id
         */
        SNOWFLAKE
    }

}
