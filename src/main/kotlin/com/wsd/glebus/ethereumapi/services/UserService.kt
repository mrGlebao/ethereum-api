package com.wsd.glebus.ethereumapi.services

import com.wsd.glebus.ethereumapi.domain.User
import com.wsd.glebus.ethereumapi.dto.UserDTO
import com.wsd.glebus.ethereumapi.exceptions.NotFoundException
import com.wsd.glebus.ethereumapi.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(var userRepository: UserRepository) {

    fun getByUsername(username: String): UserDTO {
        val user = userRepository.findOneByUsername(username)
                ?: throw NotFoundException("Can't find user for username $username")
        return UserDTO(user.username, user.password, user.authorities)
    }

    fun add(userDTO: UserDTO): Long {
        val user = User(username = userDTO.username, password = userDTO.password, roles = userDTO.roles)
        return userRepository.save(user).getId()!!
    }

}