package com.sky.user.mapper;


import com.sky.entity.User;

import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insertUser(User  user);

    @Select("select * from user where id = #{id}")
    User getById(Long id);


    int UserStatistics(Map<String, Object> map);
}
