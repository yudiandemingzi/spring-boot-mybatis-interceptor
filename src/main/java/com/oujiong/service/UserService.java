package com.oujiong.service;

import com.oujiong.vo.UserVO;

import java.util.List;

/**
 * @Description: 用户相关接口
 *
 * @author xub
 * @date 2019/8/24 下午6:32
 */
public interface UserService {

    /**
     *  批量 保存用户信息
     * @param userVOList
     */
   String  insertForeach(List<UserVO> userVOList);

    /**
     *  单个 保存用户信息
     * @param userVO
     */
    String  saveOne(UserVO userVO);

}