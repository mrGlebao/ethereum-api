package com.wsd.glebus.ethereumapi.repositories

import com.wsd.glebus.ethereumapi.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findOneByUsername(username: String): User?

}