package com.sky.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "sky.auth")
@Data
public class AuthProperties {
        private List<String> includePaths;
        private List<String> excludePaths;
}
