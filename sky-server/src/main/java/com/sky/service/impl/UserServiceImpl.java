package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
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

    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {
        String Openid = getOpenid(userLoginDTO.getCode());
        if(Openid==null) throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        User byOpenid = userMapper.getByOpenid(Openid);
        if(byOpenid==null){
             byOpenid=User.builder()
                     .openid(Openid)
                     .createTime(LocalDateTime.now())
                     .build();
             userMapper.insertUser(byOpenid);
        }
        return byOpenid;
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

            JSONObject jsonObject = JSONObject.parseObject(json);
            // 检查是否有错误码
            if(jsonObject.containsKey("errcode")) {
                log.error("微信接口错误: {}", jsonObject.getString("errmsg"));
                return null;
            }
            return jsonObject.getString("openid");
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            return null;
        }
    }


}
