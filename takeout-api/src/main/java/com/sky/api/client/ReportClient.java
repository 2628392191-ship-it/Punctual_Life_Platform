package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("report-service")
public interface ReportClient {
}
