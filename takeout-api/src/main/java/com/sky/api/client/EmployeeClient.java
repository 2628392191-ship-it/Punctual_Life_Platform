package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("employee-service")
public interface EmployeeClient {
}
