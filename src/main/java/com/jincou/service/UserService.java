package com.jincou.service;

import com.jincou.vo.UserVO;

import java.util.List;

public interface UserService {

    /**
     *  批量 保存用户信息
     * @param userVOList
     * @return
     */
   String  insertForeach(List<UserVO> userVOList);

    /**
     *  单个 保存用户信息
     * @param userVO
     * @return
     */
    String  saveOne(UserVO userVO);


}