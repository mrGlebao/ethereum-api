package com.wsd.glebus.ethereumapi.domain

import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType

@Entity
data class User(
        @Column(unique = true, name = "username")
        private val username: String,

        @Column(name = "password")
        private val password: String,

        @Column(name = "enabled")
        private val isEnabled: Boolean,

        @Column(name = "credentials_non_expired")
        private val isCredentialsNonExpired: Boolean,

        @Column(name = "account_non_expired")
        private val isAccountNonExpired: Boolean,

        @Column(name = "account_non_locked")
        private val isAccountNonLocked: Boolean,

        @Column(name = "authorities")
        @ElementCollection(targetClass = Role::class, fetch = FetchType.EAGER)
        private val authorities: Set<Role>) : AbstractJpaPersistable<Long>(), UserDetails {
    override fun getUsername(): String = username
    override fun getPassword(): String = password
    override fun isEnabled(): Boolean = isEnabled
    override fun isCredentialsNonExpired(): Boolean = isCredentialsNonExpired
    override fun isAccountNonExpired(): Boolean = isAccountNonExpired
    override fun isAccountNonLocked(): Boolean = isAccountNonLocked
    override fun getAuthorities(): Set<Role> = authorities
}

