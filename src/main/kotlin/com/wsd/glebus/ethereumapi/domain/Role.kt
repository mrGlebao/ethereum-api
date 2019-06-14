package com.wsd.glebus.ethereumapi.domain

import org.springframework.security.core.GrantedAuthority

enum class Role : GrantedAuthority {
    USER, ADMIN;

    override fun getAuthority(): String {
        return "ROLE_$name"
    }
}