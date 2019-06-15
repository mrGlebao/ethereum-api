package com.wsd.glebus.ethereumapi.domain

import org.hibernate.annotations.Type
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
data class User(
        @Column(unique = true, name = "username")
        private val username: String,

        @Column(name = "password")
        private val password: String,

        @Column(name = "enabled")
        private val isEnabled: Boolean = true,

        @Column(name = "credentials_non_expired")
        private val isCredentialsNonExpired: Boolean = true,

        @Column(name = "account_non_expired")
        private val isAccountNonExpired: Boolean = true,

        @Column(name = "account_non_locked")
        private val isAccountNonLocked: Boolean = true,

        @Column(name = "roles")
        @Type(type = "com.wsd.glebus.ethereumapi.domain.types.EnumCollectionStringType")
        private val roles: Set<Role>,
        @OneToOne
        private val credentials: UserCredentials? = null
) : AbstractJpaPersistable<Long>(), UserDetails {

    override fun getUsername(): String = username
    override fun getPassword(): String = password
    override fun isEnabled(): Boolean = isEnabled
    override fun isCredentialsNonExpired(): Boolean = isCredentialsNonExpired
    override fun isAccountNonExpired(): Boolean = isAccountNonExpired
    override fun isAccountNonLocked(): Boolean = isAccountNonLocked
    override fun getAuthorities(): Set<Role> = roles
}

