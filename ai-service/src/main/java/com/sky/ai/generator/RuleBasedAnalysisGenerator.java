package com.sky.ai.generator;

import com.sky.ai.model.AnalysisContext;
import com.sky.vo.BusinessAnalysisVO;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.BusinessRiskVO;
import com.sky.vo.BusinessSuggestionVO;
import com.sky.vo.BusinessTrendVO;
import com.sky.vo.HotSalesInsightVO;
import com.sky.vo.HotSalesItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RuleBasedAnalysisGenerator implements AnalysisGenerator {

    @Override
    public BusinessAnalysisVO generate(AnalysisContext context) {
        BusinessDataVO businessData = normalizeBusinessData(context.getBusinessData());
        BusinessTrendVO trend = buildTrend(context);
        HotSalesInsightVO hotSalesInsight = buildHotSalesInsight(context);
        List<BusinessRiskVO> risks = buildRisks(context.getPeriodType(), businessData, trend, hotSalesInsight);
        List<BusinessSuggestionVO> suggestions = buildSuggestions(risks);

        return BusinessAnalysisVO.builder()
                .periodType(context.getPeriodType())
                .beginDate(context.getBeginDate())
                .endDate(context.getEndDate())
                .summary(buildSummary(context.getPeriodType(), businessData))
                .businessData(businessData)
                .trend(trend)
                .risks(risks)
                .suggestions(suggestions)
                .hotSalesInsight(hotSalesInsight)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private BusinessDataVO normalizeBusinessData(BusinessDataVO businessData) {
        if (businessData == null) {
            return BusinessDataVO.builder()
                    .turnover(0.0)
                    .validOrderCount(0)
                    .orderCompletionRate(0.0)
                    .unitPrice(0.0)
                    .newUsers(0)
                    .build();
        }
        return BusinessDataVO.builder()
                .turnover(valueOrZero(businessData.getTurnover()))
                .validOrderCount(valueOrZero(businessData.getValidOrderCount()))
                .orderCompletionRate(valueOrZero(businessData.getOrderCompletionRate()))
                .unitPrice(valueOrZero(businessData.getUnitPrice()))
                .newUsers(valueOrZero(businessData.getNewUsers()))
                .build();
    }

    private String buildSummary(String periodType, BusinessDataVO businessData) {
        String label;
        if ("DAY".equals(periodType)) {
            label = "本日";
        } else if ("WEEK".equals(periodType)) {
            label = "近7天";
        } else if ("MONTH".equals(periodType)) {
            label = "近30天";
        } else {
            label = "当前周期";
        }
        return String.format("%s营业额为%.2f元，有效订单%d单，订单完成率%.2f%%，平均客单价%.2f元，新增用户%d人。",
                label,
                businessData.getTurnover(),
                businessData.getValidOrderCount(),
                businessData.getOrderCompletionRate() * 100,
                businessData.getUnitPrice(),
                businessData.getNewUsers());
    }

    private BusinessTrendVO buildTrend(AnalysisContext context) {
        List<String> dates = splitString(context.getTurnoverReport() == null ? null : context.getTurnoverReport().getDateList());
        List<Double> turnovers = splitDouble(context.getTurnoverReport() == null ? null : context.getTurnoverReport().getTurnoverList());
        List<Double> orders = splitDouble(context.getOrderReport() == null ? null : context.getOrderReport().getOrderCountList());
        List<Double> users = splitDouble(context.getUserReport() == null ? null : context.getUserReport().getNewUserList());

        String turnoverTrend = judgeTrend(turnovers);
        String orderTrend = judgeTrend(orders);
        String userTrend = judgeTrend(users);

        int limit = Math.min(dates.size(), turnovers.size());
        String peakDate = null;
        Double peakValue = null;
        String lowestDate = null;
        Double lowestValue = null;
        for (int i = 0; i < limit; i++) {
            Double value = turnovers.get(i);
            if (peakValue == null || value > peakValue) {
                peakValue = value;
                peakDate = dates.get(i);
            }
            if (lowestValue == null || value < lowestValue) {
                lowestValue = value;
                lowestDate = dates.get(i);
            }
        }

        return BusinessTrendVO.builder()
                .turnoverTrend(turnoverTrend)
                .orderTrend(orderTrend)
                .userTrend(userTrend)
                .turnoverPeakDate(peakDate)
                .turnoverPeakValue(peakValue)
                .turnoverLowestDate(lowestDate)
                .turnoverLowestValue(lowestValue)
                .trendSummary(buildTrendSummary(turnoverTrend, orderTrend, userTrend, peakDate, peakValue))
                .build();
    }

    private String buildTrendSummary(String turnoverTrend, String orderTrend, String userTrend, String peakDate, Double peakValue) {
        StringBuilder builder = new StringBuilder();
        builder.append("营业额整体").append(trendLabel(turnoverTrend));
        builder.append("，订单量整体").append(trendLabel(orderTrend));
        builder.append("，新增用户整体").append(trendLabel(userTrend)).append("。");
        if (peakDate != null && peakValue != null) {
            builder.append("营业额最高出现在").append(peakDate).append("，为").append(String.format("%.2f", peakValue)).append("元。");
        }
        return builder.toString();
    }

    private String trendLabel(String trend) {
        if ("UP".equals(trend)) {
            return "呈上升趋势";
        }
        if ("DOWN".equals(trend)) {
            return "呈下降趋势";
        }
        if ("STABLE".equals(trend)) {
            return "较为平稳";
        }
        return "暂无法判断趋势";
    }

    private String judgeTrend(List<Double> values) {
        if (values.size() < 2) {
            return "UNKNOWN";
        }
        int midpoint = values.size() / 2;
        List<Double> firstHalf = values.subList(0, midpoint);
        List<Double> secondHalf = values.subList(midpoint, values.size());
        double firstAverage = average(firstHalf);
        double secondAverage = average(secondHalf);
        if (secondAverage > firstAverage * 1.1) {
            return "UP";
        }
        if (secondAverage < firstAverage * 0.9) {
            return "DOWN";
        }
        return "STABLE";
    }

    private HotSalesInsightVO buildHotSalesInsight(AnalysisContext context) {
        List<String> names = splitString(context.getSalesTop10Report() == null ? null : context.getSalesTop10Report().getNameList());
        List<Integer> numbers = splitInteger(context.getSalesTop10Report() == null ? null : context.getSalesTop10Report().getNumberList());
        int limit = Math.min(names.size(), numbers.size());
        if (limit == 0) {
            return HotSalesInsightVO.builder()
                    .topGoods(Collections.emptyList())
                    .topGoodsSummary("当前周期暂无热销商品数据。")
                    .salesConcentration("UNKNOWN")
                    .insight("建议检查门店营业状态、商品上架情况和订单数据是否正常。")
                    .build();
        }

        int total = numbers.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        List<HotSalesItemVO> items = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            Integer number = valueOrZero(numbers.get(i));
            items.add(HotSalesItemVO.builder()
                    .rank(i + 1)
                    .name(names.get(i))
                    .number(number)
                    .percentage(total == 0 ? 0.0 : number * 100.0 / total)
                    .build());
        }

        double top1Percentage = items.get(0).getPercentage();
        double top3Percentage = items.stream().limit(3).mapToDouble(HotSalesItemVO::getPercentage).sum();
        String concentration;
        if (top1Percentage >= 50) {
            concentration = "HIGH";
        } else if (top3Percentage >= 70) {
            concentration = "MEDIUM";
        } else {
            concentration = "LOW";
        }

        HotSalesItemVO topItem = items.get(0);
        return HotSalesInsightVO.builder()
                .topGoods(items)
                .topGoodsSummary(String.format("本周期销量最高的是%s，共售出%d份。", topItem.getName(), topItem.getNumber()))
                .salesConcentration(concentration)
                .insight(buildHotSalesText(concentration, topItem))
                .build();
    }

    private String buildHotSalesText(String concentration, HotSalesItemVO topItem) {
        if ("HIGH".equals(concentration)) {
            return String.format("%s销量占比较高，可围绕该商品设计套餐或加购活动，同时提升其他商品曝光。", topItem.getName());
        }
        if ("MEDIUM".equals(concentration)) {
            return "热销商品主要集中在头部商品，可保持爆品供给，并观察非热销商品转化。";
        }
        return "热销商品销量分布较均衡，可继续保持多品类供给。";
    }

    private List<BusinessRiskVO> buildRisks(String periodType, BusinessDataVO businessData, BusinessTrendVO trend, HotSalesInsightVO hotSalesInsight) {
        List<BusinessRiskVO> risks = new ArrayList<>();
        if (businessData.getValidOrderCount() == 0) {
            risks.add(risk("NO_VALID_ORDER", "HIGH", "当前周期无有效订单", "当前周期没有有效订单，需要检查门店营业、商品上架或推广入口。", "有效订单数", String.valueOf(businessData.getValidOrderCount())));
        }
        if (businessData.getOrderCompletionRate() < 0.8) {
            risks.add(risk("LOW_COMPLETION_RATE", "HIGH", "订单完成率偏低", "订单完成率低于80%，可能存在取消订单较多、出餐配送效率不足等问题。", "订单完成率", String.format("%.2f%%", businessData.getOrderCompletionRate() * 100)));
        }
        if (!"DAY".equals(periodType) && businessData.getNewUsers() == 0) {
            risks.add(risk("LOW_NEW_USERS", "MEDIUM", "新增用户偏低", "当前周期新增用户为0，需要关注拉新转化效果。", "新增用户", String.valueOf(businessData.getNewUsers())));
        }
        if ("DOWN".equals(trend.getTurnoverTrend())) {
            risks.add(risk("TURNOVER_DROP", "MEDIUM", "营业额呈下降趋势", "当前周期后半段营业额低于前半段，需要关注低谷日期和活动效果。", "营业额趋势", trend.getTurnoverTrend()));
        }
        if (businessData.getUnitPrice() < 20 && businessData.getValidOrderCount() > 0) {
            risks.add(risk("LOW_UNIT_PRICE", "LOW", "客单价偏低", "平均客单价低于20元，可考虑通过套餐或加购提升客单价。", "平均客单价", String.format("%.2f", businessData.getUnitPrice())));
        }
        if ("HIGH".equals(hotSalesInsight.getSalesConcentration())) {
            risks.add(risk("HOT_SALES_CONCENTRATION", "LOW", "热销商品集中度偏高", "销量过度集中在单一商品，可能增加供应和口味偏好的波动风险。", "热销集中度", hotSalesInsight.getSalesConcentration()));
        }
        return risks;
    }

    private BusinessRiskVO risk(String code, String level, String title, String description, String metricName, String metricValue) {
        return BusinessRiskVO.builder()
                .riskCode(code)
                .riskLevel(level)
                .title(title)
                .description(description)
                .metricName(metricName)
                .metricValue(metricValue)
                .build();
    }

    private List<BusinessSuggestionVO> buildSuggestions(List<BusinessRiskVO> risks) {
        if (risks.isEmpty()) {
            return Collections.singletonList(suggestion("KEEP_STABLE_OPERATION", "LOW", "保持稳定经营", "当前经营指标整体稳定，可继续关注热销商品供给和用户复购。", null));
        }
        return risks.stream()
                .map(risk -> suggestionForRisk(risk.getRiskCode()))
                .collect(Collectors.toList());
    }

    private BusinessSuggestionVO suggestionForRisk(String riskCode) {
        if ("LOW_COMPLETION_RATE".equals(riskCode)) {
            return suggestion("IMPROVE_ORDER_FULFILLMENT", "HIGH", "提升订单履约", "建议排查取消订单原因，关注高峰期出餐、接单和配送链路。", riskCode);
        }
        if ("NO_VALID_ORDER".equals(riskCode)) {
            return suggestion("CHECK_SHOP_OPERATION", "HIGH", "检查营业链路", "建议检查门店营业状态、菜品套餐是否上架、配送范围及前端入口是否正常。", riskCode);
        }
        if ("LOW_NEW_USERS".equals(riskCode)) {
            return suggestion("PROMOTE_NEW_USERS", "MEDIUM", "提升新用户转化", "建议通过新人优惠券、满减活动或爆品推广提升新用户转化。", riskCode);
        }
        if ("TURNOVER_DROP".equals(riskCode)) {
            return suggestion("BOOST_LOW_TURNOVER_PERIOD", "MEDIUM", "改善营业额下滑", "建议对比下降日期的订单量、客单价和热销商品变化，针对低谷日期推出限时促销。", riskCode);
        }
        if ("LOW_UNIT_PRICE".equals(riskCode)) {
            return suggestion("INCREASE_UNIT_PRICE", "LOW", "提升客单价", "建议设计套餐组合、加购商品或满减门槛，提高平均客单价。", riskCode);
        }
        return suggestion("OPTIMIZE_MENU", "LOW", "优化商品结构", "建议围绕爆品设计套餐，同时优化非热销商品曝光，降低单一商品依赖。", riskCode);
    }

    private BusinessSuggestionVO suggestion(String code, String priority, String title, String content, String relatedRiskCode) {
        return BusinessSuggestionVO.builder()
                .suggestionCode(code)
                .priority(priority)
                .title(title)
                .content(content)
                .relatedRiskCode(relatedRiskCode)
                .build();
    }

    private List<String> splitString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Double> splitDouble(String value) {
        return splitString(value).stream()
                .map(this::parseDouble)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Integer> splitInteger(String value) {
        return splitString(value).stream()
                .map(this::parseInteger)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Double parseDouble(String value) {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            log.warn("经营分析解析数字失败：{}", value);
            return null;
        }
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            log.warn("经营分析解析整数失败：{}", value);
            return null;
        }
    }

    private double average(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private Double valueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private Integer valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
