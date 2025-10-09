package com.sky.controller.admin;

import com.itheima.alioss.AliOSSUtils;
import com.sky.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags="通用管理")
@Slf4j
public class CommonController {
    @Autowired
    private AliOSSUtils aliOSSUtils;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
         log.info("文件上传：{}", file);
         String path=aliOSSUtils.upload(file);
         return Result.success(path);
     }

}
