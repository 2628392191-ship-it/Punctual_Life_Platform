package com.sky.user.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.gateway.utils.common.constant.JwtClaimsConstant;
import com.sky.gateway.utils.common.constant.MessageConstant;
import com.sky.gateway.utils.common.exception.LoginFailedException;
import com.sky.gateway.utils.common.util.HttpClientUtil;
import com.sky.user.util.WeChatProperties;
import com.sky.user.mapper.UserMapper;
import com.sky.user.service.UserService;
import com.sky.vo.UserLoginVO;
import com.sky.vo.UserReportVO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
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

    @Value("${sky.jwt.user-secret-key}")
    private String userSecretKey;

    @Value("${sky.jwt.user-ttl}")
    private long userTtl;

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
    public User getById(Long id) {
        return userMapper.getById(id);
    }

    @Override
    public Integer UserStatisticsTotal(Map<String, Object> map) {
        return userMapper.UserStatistics(map);
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
        // 生成JWT令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, byOpenid.getId());
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + userTtl))
                .signWith(SignatureAlgorithm.HS256, userSecretKey.getBytes(StandardCharsets.UTF_8))
                .compact();

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
            log.info("微信接口返回: {}", json);
            JSONObject jsonObject = JSONObject.parseObject(json);
            if(jsonObject.containsKey("errcode")) {
                Integer errcode = jsonObject.getInteger("errcode");
                String errmsg = jsonObject.getString("errmsg");
                log.warn("微信接口错误: errcode={}, errmsg={}", errcode, errmsg);
                // 开发模式：使用code作为mock openid
                if (weChatProperties.getDevMode() != null && weChatProperties.getDevMode()) {
                    log.info("开发模式：使用code作为mock openid");
                    return "dev_" + code;
                }
                return null;
            }
            return jsonObject.getString("openid");
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            // 开发模式下容错
            if (weChatProperties.getDevMode() != null && weChatProperties.getDevMode()) {
                log.info("开发模式：异常降级，使用code作为mock openid");
                return "dev_" + code;
            }
            return null;
        }
    }



}
