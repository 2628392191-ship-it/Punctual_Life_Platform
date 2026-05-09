package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessTrendVO implements Serializable {

    private String turnoverTrend;

    private String orderTrend;

    private String userTrend;

    private String turnoverPeakDate;

    private Double turnoverPeakValue;

    private String turnoverLowestDate;

    private Double turnoverLowestValue;

    private String trendSummary;
}
