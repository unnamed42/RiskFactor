package com.tjh.riskfactor.config;

import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import com.tjh.riskfactor.security.ACLPermissionEvaluator;

@Configuration
@EnableGlobalMethodSecurity(
    prePostEnabled = true
)
@RequiredArgsConstructor
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final ACLPermissionEvaluator evaluator;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        val handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(evaluator);
        return handler;
    }

}
