package com.sky.ai.generator;

import com.sky.ai.model.AnalysisContext;
import com.sky.vo.BusinessAnalysisVO;

public interface AnalysisGenerator {

    BusinessAnalysisVO generate(AnalysisContext context);
}
