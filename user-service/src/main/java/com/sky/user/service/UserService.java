package com.sky.user.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.UserLoginVO;
import com.sky.vo.UserReportVO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.Map;


public interface UserService {

    UserLoginVO wxlogin(UserLoginDTO userLoginDTO);

    UserReportVO UserStatistics(LocalDate begin, LocalDate end);

    User getById(Long id);

    Integer UserStatisticsTotal(Map<String, Object> map);
}
