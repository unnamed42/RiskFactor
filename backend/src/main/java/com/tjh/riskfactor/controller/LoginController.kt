package com.tjh.riskfactor.controller

import org.springframework.http.HttpStatus
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.util.require
import com.tjh.riskfactor.security.JwtTokenProvider

/**
 * 负责用户登录信息的Controller
 */
@CrossOrigin
@RestController
class LoginController {

    @Autowired private lateinit var authManager: AuthenticationManager
    @Autowired private lateinit var provider: JwtTokenProvider

    private fun authenticate(username: String, password: String): Authentication {
        try {
            val token = UsernamePasswordAuthenticationToken(username, password)
            return authManager.authenticate(token)
        } catch (e: RuntimeException) {
            when(e) {
                is BadCredentialsException -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong password")
                is UsernameNotFoundException -> throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
                is LockedException, is DisabledException -> throw ResponseStatusException(HttpStatus.FORBIDDEN, e.message)
                else -> throw e
            }
        }
    }

    /**
     * 请求登录，返回JWT。请求JSON格式为：
     * {
     *     "username": [username],
     *     "password": [password]
     * }
     * 给予应答内容的JSON格式为：
     * {
     *     "token": [token]
     * }
     * 不需要包含多余信息，因为token中已经编码了用户名和用户id
     *
     * @param body 请求体，格式如上
     * @return token应答，格式如上
     */
    @PostMapping("/login")
    fun requestToken(@RequestBody body: Map<String, String>): String {
        val username = require(body, "username")
        val password = require(body, "password")
        val auth = authenticate(username, password)
        val token = provider.generateToken(auth)
        return "{\"token\":\"$token\"}"
    }

}
