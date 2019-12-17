package com.tjh.riskfactor.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("security.jwt")
class JwtProperties {

    /**
     * JWT的签名密钥。使用[密钥生成器](https://www.allkeysgenerator.com/)生成512位的Encryption Key
     */
    lateinit var signingKey: String

    /**
     * 没什么用
     */
    lateinit var securityRealm: String

    /**
     * JWT的有效时间（单位：小时）
     */
    lateinit var expiryHours: Number

    /**
     * 新JWT在一段时间之后才能生效（单位：毫秒）
     */
    lateinit var notBefore: Number

    /**
     * 指定[org.springframework.security.crypto.password.PasswordEncoder]的encoding strength
     */
    lateinit var encodingStrength: Number

}
