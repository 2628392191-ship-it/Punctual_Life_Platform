package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.setmeal.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
   // 正确的方式应该是构建基础URL，参数通过map传递
    private static final String url = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 统计用户数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO UserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        while(!begin.isAfter(end)){
            dateList.add(begin);
            begin=begin.plusDays(1);
        }

        List<Integer> newUserList=new ArrayList<>();
        List<Integer> totalUserList=new ArrayList<>();
        dateList.forEach(date->{
            LocalDateTime beginTime=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);
            Map<String,Object> map=new HashMap<>();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            int size = userMapper.UserStatistics(map);
            newUserList.add(size);

            map.remove("beginTime");
            int totalCount = userMapper.UserStatistics(map);
            totalUserList.add(totalCount);
        });

        return new UserReportVO(StringUtils.join(dateList, ","),
                 StringUtils.join(totalUserList, ","),
                 StringUtils.join(newUserList, ","));
    }

    @Override
    public UserLoginVO wxlogin(UserLoginDTO userLoginDTO) {
        String Openid = getOpenid(userLoginDTO.getCode());
        if(Openid==null) throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        //根据Openid查询用户
        User byOpenid = userMapper.getByOpenid(Openid);
        //若用户为空则创建用户
        if(byOpenid==null){
             byOpenid=User.builder()
                     .openid(Openid)
                     .createTime(LocalDateTime.now())
                     .build();
             userMapper.insertUser(byOpenid);
        }

        //设置token令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,byOpenid.getId());
        String token= JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );

        UserLoginVO userLoginVO=UserLoginVO.builder()
                .openid(byOpenid.getOpenid())
                .token(token)
                .id(byOpenid.getId())
                .build();
        return userLoginVO;
    }

    private String getOpenid(String code){
        Map<String,String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");

        try {
            String json = HttpClientUtil.doGet(url,map);
            // 添加日志记录
            log.info("微信接口返回: {}", json);
            //将字符串变为json数据
            JSONObject jsonObject = JSONObject.parseObject(json);
            // 检查是否有错误码
            if(jsonObject.containsKey("errcode")) {
                log.error("微信接口错误: {}", jsonObject.getString("errmsg"));
                return null;
            }
            //返回json数据中的openid
            return jsonObject.getString("openid");
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            return null;
        }
    }


}
