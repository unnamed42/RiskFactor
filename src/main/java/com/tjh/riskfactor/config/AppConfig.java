package com.tjh.riskfactor.config;

import lombok.val;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.service.DataService;

@Configuration
public class AppConfig {

    /**
     * 启用Jackson的Hibernates LAZY fetch支持
     */
    @Bean
    protected Module module() {
        val module = new Hibernate5Module();
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        return module;
    }

    /**
     * 初始化数据库数据
     */
    @Bean
    CommandLineRunner runner(DataService service) {
        return new CommandLineRunner() {
            @Override @Transactional
            public void run(String... args) throws Exception {
                service.init();
            }
        };
    }

}
