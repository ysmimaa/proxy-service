package com.proxy.service;

import brave.sampler.Sampler;
import com.proxy.service.entity.Role;
import com.proxy.service.entity.User;
import com.proxy.service.service.AccountService;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
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
