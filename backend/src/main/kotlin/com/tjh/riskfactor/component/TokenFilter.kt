package com.tjh.riskfactor.component

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException

import org.springframework.stereotype.Component
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.core.context.SecurityContextHolder

import com.tjh.riskfactor.controller.ErrorHandler
import com.tjh.riskfactor.service.TokenProvider

import java.lang.IllegalArgumentException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TokenFilter(
    private val provider: TokenProvider,
    private val handler: ErrorHandler
): OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val ctx = SecurityContextHolder.getContext()
        try {
            provider.resolveToken(req)?.let { ctx.authentication = provider.getAuthentication(it) }
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
