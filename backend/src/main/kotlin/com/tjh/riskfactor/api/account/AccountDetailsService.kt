package com.tjh.riskfactor.api.account

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

import au.com.console.jpaspecificationdsl.equal

/**
 * 自定义的[UserDetails]. 不使用自带的builder [org.springframework.security.core.userdetails.User]是因为默认实现中没有用户id.
 */
class AccountDetails(val dbUser: User, group: String?): UserDetails {

    private val authorities: List<SimpleGrantedAuthority> =
        if(group != null) listOf(SimpleGrantedAuthority(group)) else listOf()

    val id get() = dbUser.id
    val groupName: String? get() = authorities.firstOrNull()?.authority

    override fun getUsername() = dbUser.username
    override fun getPassword() = dbUser.password
    override fun getAuthorities() = authorities

    override fun isEnabled() = true
    override fun isAccountNonExpired() = true
    override fun isCredentialsNonExpired() = true
    override fun isAccountNonLocked() = authorities.isNotEmpty()
}

@Service("userDetailsService")
class AccountDetailsService(private val accounts: AccountService): UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = accounts.users.findOne(User::username.equal(username)).orElseThrow{
            UsernameNotFoundException("user [$username] not found")
        }
        val groupName = accounts.getGroupName(user.groupId)
        return AccountDetails(user, groupName)
    }

}
