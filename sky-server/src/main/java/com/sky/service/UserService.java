package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;
import org.apache.ibatis.annotations.Mapper;


public interface UserService {

    User wxlogin(UserLoginDTO userLoginDTO);


}
