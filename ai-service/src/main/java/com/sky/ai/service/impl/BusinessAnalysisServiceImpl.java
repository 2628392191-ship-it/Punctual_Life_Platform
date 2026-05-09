package com.sky.ai.service.impl;

import com.sky.ai.generator.AnalysisGenerator;
import com.sky.ai.model.AnalysisContext;
import com.sky.ai.service.BusinessAnalysisService;
import com.sky.api.client.ReportClient;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.BusinessAnalysisVO;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class BusinessAnalysisServiceImpl implements BusinessAnalysisService {

    private static final int MAX_RANGE_DAYS = 31;

    @Resource
    private ReportClient reportClient;

    @Resource
    private AnalysisGenerator analysisGenerator;

    @Override
    public BusinessAnalysisVO summary(String type, LocalDate date) {
        LocalDate end = date == null ? LocalDate.now() : date;
        String periodType = normalizePeriodType(type);
        LocalDate begin;
        if ("WEEK".equals(periodType)) {
            begin = end.minusDays(6);
        } else if ("MONTH".equals(periodType)) {
            begin = end.minusDays(29);
        } else {
            begin = end;
        }
        return analyze(periodType, begin, end);
    }

    @Override
    public BusinessAnalysisVO range(LocalDate begin, LocalDate end) {
        validateRange(begin, end);
        return analyze("RANGE", begin, end);
    }

    private BusinessAnalysisVO analyze(String periodType, LocalDate begin, LocalDate end) {
        validateRange(begin, end);
        BusinessDataVO businessData = getData(reportClient.businessData(begin, end), "经营汇总数据");
        TurnoverReportVO turnoverReport = getData(reportClient.turnoverStatistics(begin, end), "营业额统计");
        UserReportVO userReport = getData(reportClient.userStatistics(begin, end), "用户统计");
        OrderReportVO orderReport = getData(reportClient.ordersStatistics(begin, end), "订单统计");
        SalesTop10ReportVO salesTop10Report = getData(reportClient.top10(begin, end), "销量Top10");

        AnalysisContext context = AnalysisContext.builder()
                .periodType(periodType)
                .beginDate(begin)
                .endDate(end)
                .businessData(businessData)
                .turnoverReport(turnoverReport)
                .userReport(userReport)
                .orderReport(orderReport)
                .salesTop10Report(salesTop10Report)
                .build();
        return analysisGenerator.generate(context);
    }

    private String normalizePeriodType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return "DAY";
        }
        String periodType = type.trim().toUpperCase();
        if (!"DAY".equals(periodType) && !"WEEK".equals(periodType) && !"MONTH".equals(periodType)) {
            throw new IllegalArgumentException("分析周期仅支持 DAY、WEEK、MONTH");
        }
        return periodType;
    }

    private void validateRange(LocalDate begin, LocalDate end) {
        if (begin == null || end == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }
        if (begin.isAfter(end)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        if (ChronoUnit.DAYS.between(begin, end) + 1 > MAX_RANGE_DAYS) {
            throw new IllegalArgumentException("分析日期范围不能超过31天");
        }
    }

    private <T> T getData(Result<T> result, String name) {
        if (result == null) {
            throw new IllegalStateException(name + "返回为空");
        }
        if (result.getCode() == null || result.getCode() != 1) {
            throw new IllegalStateException(name + "获取失败：" + result.getMsg());
        }
        return result.getData();
    }
}
