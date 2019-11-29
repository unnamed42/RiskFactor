package com.tjh.riskfactor.security

import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.service.UserService

@Service
class JwtUserDetailsService(private val service: UserService): UserDetailsService {

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user = service.find(username) ?: throw UsernameNotFoundException("user [$username] not found")
        return JwtUserDetails(user, user.group.name)
    }

}
