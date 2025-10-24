package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;
import com.sky.vo.UserReportVO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;


public interface UserService {

    User wxlogin(UserLoginDTO userLoginDTO);

    UserReportVO UserStatistics(LocalDate begin, LocalDate end);
}
