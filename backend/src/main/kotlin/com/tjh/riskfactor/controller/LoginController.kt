package com.tjh.riskfactor.controller

import org.springframework.http.HttpStatus
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.util.require
import com.tjh.riskfactor.security.JwtTokenProvider

/**
 * 负责用户登录信息的Controller
 */
@CrossOrigin
@RestController
class LoginController(
    private val authManager: AuthenticationManager,
    private val provider: JwtTokenProvider
) {

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
     * <pre><code>
     * {
     *     "username": [username],
     *     "password": [password]
     * }
     * </code></pre>
     * 给予应答内容的JSON格式为：
     * <pre><code>
     * {
     *     "token": [token]
     * }
     * </code></pre>
     * 不需要包含多余信息，因为token中已经编码了用户名和用户id
     *
     * @param body 请求体，格式如上
     * @return token应答，格式如上
     */
    @PostMapping("/login")
    fun requestToken(@RequestBody body: Map<String, String>): Map<String, String> {
        val username = require(body, "username")
        val password = require(body, "password")
        val auth = authenticate(username, password)
        return mapOf("token" to provider.generateToken(auth))
    }

    /**
     * 已登录用户用这个请求来刷新自己的token以避免token过时失效
     */
    @GetMapping("/login/refresh")
    fun refreshToken(auth: Authentication): Map<String, String> {
        return mapOf("token" to provider.generateToken(auth))
    }

}
