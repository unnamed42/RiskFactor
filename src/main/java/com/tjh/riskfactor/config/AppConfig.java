package com.tjh.riskfactor.config;

import lombok.val;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class AppConfig {

    @Value("${security.jwt.encoding-strength}")
    private Integer strength;

    /**
     * This bean must not be put in {@code SecurityConfig}, otherwise it would cause dependency loop
     */
    @Bean @Primary
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    protected Module module() {
        return new Hibernate5Module();
    }

    @Bean
    protected FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
        val source = new UrlBasedCorsConfigurationSource();
        val config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://127.0.0.1:8080"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        val bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
