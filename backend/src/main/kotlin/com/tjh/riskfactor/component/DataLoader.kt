package com.tjh.riskfactor.component

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

import org.springframework.stereotype.Component
import org.springframework.boot.CommandLineRunner
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.service.AccountService
import com.tjh.riskfactor.service.ConsoleService
import com.tjh.riskfactor.service.SchemaService

import java.io.InputStream

@Component
class DataLoader(
    private val accounts: AccountService,
    private val schemas: SchemaService,
    private val console: ConsoleService,
    builder: Jackson2ObjectMapperBuilder
): CommandLineRunner {

    // 支持读取yaml格式的资源文件
    private val mapper: ObjectMapper = builder.factory(YAMLFactory()).build()

    @Transactional
    override fun run(vararg args: String?) {
        onceOnly("users") {
            accounts.loadFromSchema(mapper.parse(it))
        }
        onceOnly("rules") {
            schemas.loadFromSchema(mapper.parse(it))
        }
    }

    private inline fun onceOnly(onceKey: String, action: (InputStream) -> Unit) {
        if (console[onceKey] != null)
            return
        javaClass.getResourceAsStream("/data/$onceKey.yml").use(action)
        console[onceKey] = "1"
    }

    private inline fun <reified T> ObjectMapper.parse(input: InputStream): T {
        val type = object: TypeReference<T>() {}
        return this.readValue(input, type)
    }

}
