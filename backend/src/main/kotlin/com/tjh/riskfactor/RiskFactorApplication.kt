package com.tjh.riskfactor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

import com.tjh.riskfactor.security.JwtProperties

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class RiskFactorApplication

fun main(args: Array<String>) {
    runApplication<RiskFactorApplication>(*args)
}
