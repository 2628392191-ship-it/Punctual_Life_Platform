package com.sky.ai.controller.admin;

import com.sky.ai.service.BusinessAnalysisService;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.BusinessAnalysisVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

@Api(tags = "管理端-AI经营分析")
@Slf4j
@RestController
@RequestMapping("/admin/analysis")
public class
BusinessAnalysisController {

    @Resource
    private BusinessAnalysisService businessAnalysisService;

    @ApiOperation("经营分析摘要")
    @GetMapping("/summary")
    public Result<BusinessAnalysisVO> summary(@RequestParam(required = false) String type,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        log.info("经营分析摘要：type={}, date={}", type, date);
        try {
            return Result.success(businessAnalysisService.summary(type, date));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("区间经营分析")
    @GetMapping("/range")
    public Result<BusinessAnalysisVO> range(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("区间经营分析：{}到{}", begin, end);
        try {
            return Result.success(businessAnalysisService.range(begin, end));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }
}
