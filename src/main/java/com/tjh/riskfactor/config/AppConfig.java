package com.tjh.riskfactor.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    /**
     * 启用Jackson的Hibernates LAZY fetch支持
     */
    @Bean
    protected Module module() {
        return new Hibernate5Module();
    }

}
