package com.sky.gateway.utils.common.config;


import com.sky.gateway.utils.common.interceptor.JwtTokenAdminInterceptor;
import com.sky.gateway.utils.common.interceptor.JwtTokenUserInterceptor;
import com.sky.gateway.utils.common.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Slf4j
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Bean
    JwtTokenAdminInterceptor jwtTokenAdminInterceptor() {
        return new JwtTokenAdminInterceptor();
    }

    @Bean
    JwtTokenUserInterceptor jwtTokenUserInterceptor() {
        return new JwtTokenUserInterceptor();
    }
    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor());
        registry.addInterceptor(jwtTokenUserInterceptor());
    }


    //配置转化器，将json数据格式转换成对象
    //转换日期格式的配置
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters){
        log.info("扩展消息转换器...");
        //构建一个转换器
        MappingJackson2HttpMessageConverter mc=new MappingJackson2HttpMessageConverter();
        //设置转化器规则
        //处理日期格式的转换 --只是其中一种
        mc.setObjectMapper(new JacksonObjectMapper());
        //吧这个消息转化器添加到converters中
        converters.add(0,mc);
    }

    /**
     * 设置静态资源映射
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
