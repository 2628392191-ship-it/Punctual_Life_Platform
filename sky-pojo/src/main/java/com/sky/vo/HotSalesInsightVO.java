package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotSalesInsightVO implements Serializable {

    private List<HotSalesItemVO> topGoods;

    private String topGoodsSummary;

    private String salesConcentration;

    private String insight;
}
