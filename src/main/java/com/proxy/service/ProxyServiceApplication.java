package com.proxy.service;

import brave.sampler.Sampler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
public class ProxyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProxyServiceApplication.class, args);
    }

    @Bean
    public Sampler getSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

}
