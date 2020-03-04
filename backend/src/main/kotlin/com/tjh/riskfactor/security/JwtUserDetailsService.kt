package com.tjh.riskfactor.security

import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.repo.UserRepository

@Service
class JwtUserDetailsService(private val repo: UserRepository): UserDetailsService {

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user = repo.findByName(username) ?: throw UsernameNotFoundException("user [$username] not found")
        return JwtUserDetails(user, user.group.name)
    }

}
