package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..))")
    public void autoFillPointcut() {}

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        //该增强为为每条数据的更改或添加自动增加创建或修改时间、操作员工id
        log.info("开始进行数据填充");
        //获取方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //拿到注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);

        //获取注解中的操作类型并且将值给枚举类型operationType
        if(autoFill == null) return;
        //通过枚举类拿到相应的数据库操作类型
        OperationType operationType = autoFill.value();


        //获取方法中参数，并且把参数第一个赋值给object（因为此处是填充一个对象的属性）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;
        Object object = args[0];

        //把当前时间、操作员工的管理员id设置到对象中
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //判断操作类型从而进行填充
        if (operationType == OperationType.INSERT) {
            try {
                //将当前时间、操作员工id设置到方法对象中
                //获取设置创建、更新时间，员工id的set方法给方法对象
                Method setCreateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //invoke()方法将参数传入，进行数据填充
                //通过调用方法对象的对当前要增强的方法进行数据填充
                setCreateTime.invoke(object, now);
                setUpdateTime.invoke(object, now);
                setCreateUser.invoke(object, currentId);
                setUpdateUser.invoke(object, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateTime.invoke(object, now);
                setUpdateUser.invoke(object, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
