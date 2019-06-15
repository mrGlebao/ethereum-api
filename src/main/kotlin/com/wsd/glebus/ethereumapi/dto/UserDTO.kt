package com.wsd.glebus.ethereumapi.dto

import com.wsd.glebus.ethereumapi.domain.Role
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class UserDTO(

        @NotNull
        val username: String,

        @NotNull
        val password: String,

        @NotEmpty
        val roles: Set<Role>) {
}