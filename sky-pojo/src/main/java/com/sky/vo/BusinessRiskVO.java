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
public class BusinessRiskVO implements Serializable {

    private String riskCode;

    private String riskLevel;

    private String title;

    private String description;

    private String metricName;

    private String metricValue;
}
