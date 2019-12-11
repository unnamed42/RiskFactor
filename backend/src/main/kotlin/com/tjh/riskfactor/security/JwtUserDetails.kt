package com.tjh.riskfactor.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import com.tjh.riskfactor.entity.User

/**
 * 自定义的[UserDetails]. 不使用自带的builder [org.springframework.security.core.userdetails.User]是因为默认实现中没有用户id.
 */
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
