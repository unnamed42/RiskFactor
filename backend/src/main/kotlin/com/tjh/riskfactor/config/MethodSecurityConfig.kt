package com.tjh.riskfactor.config

import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

import com.tjh.riskfactor.security.PermissionEvaluator

@Configuration
@EnableGlobalMethodSecurity(
    prePostEnabled = true
)
class MethodSecurityConfig: GlobalMethodSecurityConfiguration() {

    @Autowired lateinit var evaluator: PermissionEvaluator

    @Bean @Primary
    fun e() = evaluator

}
