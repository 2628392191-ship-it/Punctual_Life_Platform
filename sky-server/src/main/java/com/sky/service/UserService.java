package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.vo.UserLoginVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;


public interface UserService {

    UserLoginVO wxlogin(UserLoginDTO userLoginDTO);

    UserReportVO UserStatistics(LocalDate begin, LocalDate end);
}
