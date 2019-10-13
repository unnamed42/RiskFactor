package com.tjh.riskfactor.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import com.tjh.riskfactor.security.PermissionEvaluator;

@Configuration
@EnableGlobalMethodSecurity(
    prePostEnabled = true
)
@RequiredArgsConstructor
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final PermissionEvaluator evaluator;

    @Bean(name = "e")
    public PermissionEvaluator permissionEvaluator() {
        return evaluator;
    }

}
