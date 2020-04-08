package com.tjh.riskfactor.config

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.fasterxml.jackson.module.kotlin.KotlinModule

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import com.tjh.riskfactor.controller.ErrorHandler
import com.tjh.riskfactor.component.TokenFilter
import com.tjh.riskfactor.service.AccountDetailsService

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true
)
class ApplicationConfig(
    private val userDetailsService: AccountDetailsService,
    private val tokenFilter: TokenFilter,
    private val handler: ErrorHandler,
    @Value("\${jwt.encoding-strength}") private val encodingStrength: Int
): WebSecurityConfigurerAdapter(), WebMvcConfigurer {
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
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(encodingStrength)

    /**
     * 将其暴露为[Bean]
     */
    @Bean
    override fun authenticationManager() = super.authenticationManager()!!

    /**
     * 配置数据库用户验证功能，主要是为了保留[org.springframework.security.core.userdetails.UsernameNotFoundException]
     */
    @Bean
    fun daoAuthenticationProvider() = DaoAuthenticationProvider().apply {
        setUserDetailsService(userDetailsService)
        setPasswordEncoder(passwordEncoder())
        isHideUserNotFoundExceptions = false
    }

    /**
     * CORS shut up
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        super.addCorsMappings(registry)
        registry.addMapping("/**")
            .allowCredentials(true)
            .allowedMethods("*")
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(daoAuthenticationProvider())
    }

    override fun configure(http: HttpSecurity) {
        http.cors().and()
            .httpBasic().disable()
            .csrf().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/token").permitAll()
                .antMatchers("/test", "/test/**").permitAll()
                .anyRequest().authenticated().and()
            .exceptionHandling()
                .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler { req, res, ex -> handler.respondException(req, res, ex, HttpStatus.FORBIDDEN); }.and()
            .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

}
