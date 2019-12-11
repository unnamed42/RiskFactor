package com.tjh.riskfactor.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("security.jwt")
class JwtProperties {

    /**
     * JWT的签名密钥。
     */
    lateinit var signingKey: String
    lateinit var securityRealm: String
    lateinit var expiryHours: Number
    lateinit var notBefore: Number
    lateinit var encodingStrength: Number

}
