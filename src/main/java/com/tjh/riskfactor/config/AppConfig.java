package com.tjh.riskfactor.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.tjh.riskfactor.service.DataService;

@Configuration
public class AppConfig {

    @Value("${security.jwt.encoding-strength}")
    private Integer strength;

    /**
     * 启用Jackson的Hibernates LAZY fetch支持
     */
    @Bean
    protected Module module() {
        final var module = new Hibernate5Module();
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        return module;
    }

    /**
     * 默认密码哈希。Spring的BCrypt哈希实现已经包含了密码加盐
     */
    @Bean @Primary
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(strength);
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
