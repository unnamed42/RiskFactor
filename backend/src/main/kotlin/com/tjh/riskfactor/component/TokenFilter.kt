package com.tjh.riskfactor.component

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException

import org.springframework.stereotype.Component
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.core.context.SecurityContextHolder

import com.tjh.riskfactor.controller.ErrorHandler
import com.tjh.riskfactor.service.TokenService

import java.lang.IllegalArgumentException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 检查请求中是否带有JWT，有的话放进权限信息中，没有则继续[FilterChain]。除了公开可访问的API之外，均拒绝请求。
 */
@Component
class TokenFilter(
    private val service: TokenService,
    private val handler: ErrorHandler
): OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val ctx = SecurityContextHolder.getContext()
        try {
            service.resolveToken(req)?.let { ctx.authentication = service.getAuthentication(it) }
        } catch (ex: RuntimeException) {
            when(ex) {
                is IllegalArgumentException, is MalformedJwtException, is ExpiredJwtException
                    -> handler.respondException(req, res, ex, HttpStatus.UNAUTHORIZED)
                else -> throw ex
            }
        }
        chain.doFilter(req, res)
    }
}
