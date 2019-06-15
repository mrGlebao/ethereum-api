package com.wsd.glebus.ethereumapi.controllers

import com.wsd.glebus.ethereumapi.dto.UserDTO
import com.wsd.glebus.ethereumapi.services.UserService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {

    @GetMapping("/get/by-username")
    fun getByUsername(@RequestParam("username") username: String): UserDTO {
        return userService.getByUsername(username)
    }

    @PostMapping("/add")
    fun add(@RequestBody @Valid user: UserDTO): Long {
        return userService.add(user)
    }

}