package com.tjh.riskfactor.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.core.context.SecurityContextHolder

import com.tjh.riskfactor.error.ErrorResponder

import java.lang.IllegalArgumentException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtTokenFilter: OncePerRequestFilter() {

    @Autowired private lateinit var provider: JwtTokenProvider
    @Autowired private lateinit var e: ErrorResponder

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        try {
            provider.resolveToken(req)?.let { provider.getAuthentication(it) }
                ?.let { SecurityContextHolder.getContext().authentication = it }
            chain.doFilter(req, res)
        } catch (ex: RuntimeException) {
            when(ex) {
                is IllegalArgumentException, is MalformedJwtException
                    -> e.response(req, res, ex, HttpStatus.UNAUTHORIZED, "malformed token")
                is ExpiredJwtException
                    -> e.response(req, res, ex, HttpStatus.UNAUTHORIZED, "token expired")
                else -> throw ex
            }
        }
    }
}
