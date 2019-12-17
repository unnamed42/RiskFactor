package com.tjh.riskfactor.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException

import org.springframework.stereotype.Component
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.core.context.SecurityContextHolder

import com.tjh.riskfactor.error.ErrorResponder

import java.lang.IllegalArgumentException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtTokenFilter(
    private val provider: JwtTokenProvider,
    private val e: ErrorResponder
): OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        try {
            provider.resolveToken(req)?.let {
                val auth = provider.getAuthentication(it)
                SecurityContextHolder.getContext().authentication = auth
            }
        } catch (ex: RuntimeException) {
            when(ex) {
                is IllegalArgumentException, is MalformedJwtException
                    -> e.response(req, res, ex, HttpStatus.UNAUTHORIZED, "malformed token")
                is ExpiredJwtException
                    -> e.response(req, res, ex, HttpStatus.UNAUTHORIZED, "token expired")
                else -> throw ex
            }
        }
        chain.doFilter(req, res)
    }
}
