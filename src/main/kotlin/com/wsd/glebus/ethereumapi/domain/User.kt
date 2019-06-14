package com.wsd.glebus.ethereumapi.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType

@Entity
data class User(
        private val username: String,
        private val password: String,
        private val isEnabled: Boolean,
        private val isCredentialsNonExpired: Boolean,
        private val isAccountNonExpired: Boolean,
        private val isAccountNonLocked: Boolean,
        @ElementCollection(targetClass = Role::class, fetch = FetchType.EAGER)
        private val authorities: Set<GrantedAuthority>) : AbstractJpaPersistable<Long>(), UserDetails {
    override fun getUsername(): String = username
    override fun getPassword(): String = password
    override fun isEnabled(): Boolean = isEnabled
    override fun isCredentialsNonExpired(): Boolean = isCredentialsNonExpired
    override fun isAccountNonExpired(): Boolean = isAccountNonExpired
    override fun isAccountNonLocked(): Boolean = isAccountNonLocked
    override fun getAuthorities(): Set<GrantedAuthority> = authorities
}

