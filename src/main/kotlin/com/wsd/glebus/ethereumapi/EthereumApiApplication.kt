package com.wsd.glebus.ethereumapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EthereumApiApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<EthereumApiApplication>(*args)
        }
    }
}
