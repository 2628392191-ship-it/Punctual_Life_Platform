package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("category-service")
public interface CategoryClient {
}
