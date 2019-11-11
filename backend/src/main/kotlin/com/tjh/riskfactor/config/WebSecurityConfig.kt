package com.tjh.riskfactor.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import com.tjh.riskfactor.error.ErrorResponder
import com.tjh.riskfactor.security.JwtTokenFilter
import com.tjh.riskfactor.security.JwtUserDetailsService

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    @Autowired lateinit var userDetailsService: JwtUserDetailsService
    @Autowired lateinit var encoder: PasswordEncoder
    @Autowired lateinit var jwtTokenFilter: JwtTokenFilter
    @Autowired lateinit var e: ErrorResponder

    /**
     * 将其暴露为{@code Bean}
     */
    @Bean
    override fun authenticationManager() = super.authenticationManager()

    /**
     * 配置数据库用户验证功能，主要是为了保留{@link UsernameNotFoundException}
     */
    @Bean
    fun daoAuthenticationProvider() = DaoAuthenticationProvider().apply {
        setUserDetailsService(userDetailsService)
        setPasswordEncoder(encoder)
        isHideUserNotFoundExceptions = false
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.authenticationProvider(daoAuthenticationProvider())
    }

    override fun configure(http: HttpSecurity?) {
        if(http == null) return
        http.cors().and()
            .httpBasic().disable()
            .csrf().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers("/test", "/test/**").permitAll()
                .anyRequest().authenticated().and()
            .exceptionHandling()
                .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler { req, res, ex -> e.response(req, res, ex, HttpStatus.FORBIDDEN); }.and()
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

}
