package com.tjh.riskfactor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.boot.runApplication

import th.co.geniustree.springdata.jpa.repository.support.JpaSpecificationExecutorWithProjectionImpl

import com.tjh.riskfactor.config.JwtProperties

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
@EnableJpaRepositories(repositoryBaseClass = JpaSpecificationExecutorWithProjectionImpl::class)
class RiskFactorApplication

fun main(args: Array<String>) {
    runApplication<RiskFactorApplication>(*args)
}
