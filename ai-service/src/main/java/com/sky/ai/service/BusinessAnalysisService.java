package com.sky.ai.service;

import com.sky.vo.BusinessAnalysisVO;

import java.time.LocalDate;

public interface BusinessAnalysisService {

    BusinessAnalysisVO summary(String type, LocalDate date);

    BusinessAnalysisVO range(LocalDate begin, LocalDate end);
}
