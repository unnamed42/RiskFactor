package com.tjh.riskfactor.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("jwt")
data class JwtProperties(
    /**
     * JWT的签名密钥。使用 [密钥生成器](https://www.allkeysgenerator.com/)生成512位的Encryption Key
     */
    var signingKey: String = "",

    /**
     * 没什么用
     */
    var securityRealm: String = "",

    /**
     * JWT的有效时间（单位：小时）
     */
    var expiryHours: Int = 0,

    /**
     * 新JWT在一段时间之后才能生效（单位：毫秒）
     */
    var notBefore: Int = 0,

    /**
     * 指定[org.springframework.security.crypto.password.PasswordEncoder]的encoding strength
     */
    var encodingStrength: Int = 0
)
