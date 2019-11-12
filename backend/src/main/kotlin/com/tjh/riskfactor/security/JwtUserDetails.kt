package com.tjh.riskfactor.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import com.tjh.riskfactor.entity.User

class JwtUserDetails(val user: User, group: String): UserDetails {

    private val authorities: List<SimpleGrantedAuthority> = listOf(SimpleGrantedAuthority(group))

    val id get() = user.id
    val isRoot get() = isInGroup("root")

    override fun getUsername() = user.username
    override fun getPassword() = user.password
    override fun getAuthorities() = authorities

    override fun isEnabled() = true
    override fun isAccountNonExpired() = true
    override fun isCredentialsNonExpired() = true
    override fun isAccountNonLocked() = !isInGroup("nobody")

    private fun isInGroup(group: String) = authorities.any { it.authority.startsWith(group) }
}
