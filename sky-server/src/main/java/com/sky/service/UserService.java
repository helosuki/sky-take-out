package com.sky.service;


import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;


import java.io.IOException;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);
}
