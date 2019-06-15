package com.wsd.glebus.ethereumapi.domain

import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import java.math.BigInteger
import javax.persistence.Column
import javax.persistence.Entity

@Entity
data class UserCredentials(
        @Column(unique = true, name = "address")
        private val address: String?,

        @Column(name = "private_key")
        private val privateKey: BigInteger,

        @Column(name = "public_key")
        private val publicKey: BigInteger) : AbstractJpaPersistable<Long>() {
    fun toCredentials(): Credentials {
        return Credentials.create(ECKeyPair(privateKey, publicKey))
    }

}