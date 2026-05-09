package com.sky.ai.model;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisContext {

    private String periodType;

    private LocalDate beginDate;

    private LocalDate endDate;

    private BusinessDataVO businessData;

    private TurnoverReportVO turnoverReport;

    private OrderReportVO orderReport;

    private UserReportVO userReport;

    private SalesTop10ReportVO salesTop10Report;
}
