package com.tjh.riskfactor.security

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

import com.tjh.riskfactor.service.UserService

@Service
class JwtUserDetailsService: UserDetailsService {

    @Autowired private lateinit var service: UserService

    override fun loadUserByUsername(username: String): UserDetails {
        val user = service.find(username) ?: throw UsernameNotFoundException("user [$username] not found")
        val gname = service.groupNameOf(user.id) ?: "nobody"
        return JwtUserDetails(user, gname)
    }
}
