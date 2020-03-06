package com.tjh.riskfactor.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import com.tjh.riskfactor.api.error.ErrorHandler
import com.tjh.riskfactor.api.token.TokenFilter
import com.tjh.riskfactor.api.account.AccountDetailsService

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true
)
class WebSecurityConfig(
    private val userDetailsService: AccountDetailsService,
    private val encoder: PasswordEncoder,
    private val tokenFilter: TokenFilter,
    private val handler: ErrorHandler
): WebSecurityConfigurerAdapter() {

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
        setPasswordEncoder(encoder)
        isHideUserNotFoundExceptions = false
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
