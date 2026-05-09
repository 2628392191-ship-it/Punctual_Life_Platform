package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAnalysisVO implements Serializable {

    private String periodType;

    private LocalDate beginDate;

    private LocalDate endDate;

    private String summary;

    private BusinessDataVO businessData;

    private BusinessTrendVO trend;

    private List<BusinessRiskVO> risks;

    private List<BusinessSuggestionVO> suggestions;

    private HotSalesInsightVO hotSalesInsight;

    private LocalDateTime generatedAt;
}
