package com.oujiong.controller;

import com.google.common.collect.Lists;
import com.oujiong.service.UserService;
import com.oujiong.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Description: 接口测试
 *
 * @author xub
 * @date 2019/8/24 下午6:31
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 模拟插入数据
     */
    List<UserVO> userVOList = Lists.newArrayList();

    /**
     * 初始化插入数据
     */
    @PostConstruct
    private void getData() {
        userVOList.add(new UserVO("小小", "女", 3));
        userVOList.add(new UserVO("爸爸", "男", 33));
        userVOList.add(new UserVO("妈妈", "女", 33));
        userVOList.add(new UserVO("爷爷", "男", 66));
        userVOList.add(new UserVO("奶奶", "女", 66));
    }

    /**
     * @Description: 批量保存用户接口
     */
    @PostMapping("save-foreach-user")
    public Object save() {
        return userService.insertForeach(userVOList);
    }

    /**
     * @Description: 单个保存用户接口
     */
    @PostMapping("save-one-user")
    public Object saveOne() {
        return userService.saveOne(new UserVO("小小", "女", 3));
    }

}
