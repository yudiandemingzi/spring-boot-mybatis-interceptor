package com.jincou.service.impl;

import com.google.common.collect.Lists;
import com.jincou.entity.TabUser;
import com.jincou.mapper.UserMapper;
import com.jincou.service.UserService;
import com.jincou.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author xub
 * @Description: 用户实现类
 * @date 2019/8/8 上午9:13
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String insertForeach(List<UserVO> userVOList) {
        //实体转换
        List<TabUser> tabUserList = Lists.newArrayListWithCapacity(userVOList.size());
        for (UserVO userVO : userVOList) {
            TabUser tabUser = build(userVO);
            tabUserList.add(tabUser);
        }
        //批量插入数据
        userMapper.insertForeach(tabUserList);
        return "保存成功";
    }

    @Override
    public String saveOne(UserVO userVO) {
        TabUser tabUser = build(userVO);
        userMapper.insert(tabUser);
        return "保存成功";
    }

    /**
     * 实体转换
     */
    private TabUser build(UserVO vo) {
        TabUser tabUser = new TabUser();
        tabUser.setName(vo.getName());
        tabUser.setSex(vo.getSex());
        tabUser.setAge(vo.getAge());
        tabUser.setCreateTime(new Date());
        tabUser.setUpdateTime(new Date());
        tabUser.setStatus(1);
        return tabUser;

    }
}
