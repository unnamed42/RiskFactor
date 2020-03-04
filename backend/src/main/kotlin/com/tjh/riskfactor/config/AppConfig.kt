package com.tjh.riskfactor.config

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.fasterxml.jackson.module.kotlin.KotlinModule

import org.springframework.boot.CommandLineRunner
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import com.tjh.riskfactor.component.InitialDataLoader

/**
 * 一些乱七八糟的Bean都放在这里
 */
@Configuration
class AppConfig {

    @Value("\${security.jwt.encoding-strength}")
    private lateinit var encodingStrength: Number

    /**
     * 启用Jackson的Hibernates LAZY fetch支持
     */
    @Bean
    fun hibernate5Module() = Hibernate5Module().apply {
        disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION)
    }

    @Bean
    fun kotlinModule() = KotlinModule()

    /**
     * 默认密码哈希。Spring的BCrypt哈希实现已经包含了密码加盐
     */
    @Bean @Primary
    fun passwordEncoder() = BCryptPasswordEncoder(encodingStrength.toInt())

    /**
     * 初始化数据库数据
     *
     * 本可以使用lambda，但是kotlin的lambda对应jvm是`final`的，且不能加`open`，只能另外弄一个class
     */
    @Bean
    fun runner(service: InitialDataLoader) = Runner(service)

    open class Runner(val service: InitialDataLoader): CommandLineRunner {
        @Transactional
        override fun run(vararg args: String?) = service.init()
    }
}
