package com.tjh.riskfactor.controller

import com.tjh.riskfactor.service.TokenProvider
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.bind.annotation.*

/**
 * 负责用户登录信息的Controller
 */
@CrossOrigin
@RestController
class TokenController(
    private val authManager: AuthenticationManager,
    private val provider: TokenProvider
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
     * ```
     * {
     *     "username": [username],
     *     "password": [password]
     * }
     * ```
     * 给予应答内容的JSON格式为：
     * ```
     * {
     *     "token": [token]
     * }
     * ```
     * 不需要包含多余信息，因为token中已经编码了用户名和用户id
     *
     * @param body 请求体，格式如上
     * @return token应答，格式如上
     */
    @PostMapping("/token")
    fun requestToken(@RequestBody body: TokenRequest): TokenResponse {
        val (username, password) = body
        val auth = authenticate(username, password)
        return TokenResponse(provider.generateToken(auth))
    }

    /**
     * 已登录用户用这个请求来刷新自己的token以避免token过时失效
     */
    @GetMapping("/token")
    fun refreshToken(auth: Authentication): TokenResponse =
        TokenResponse(provider.generateToken(auth))
}

data class TokenRequest(
    val username: String,
    val password: String
)

data class TokenResponse(val token: String)
