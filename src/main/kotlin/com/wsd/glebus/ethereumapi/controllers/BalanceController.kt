package com.wsd.glebus.ethereumapi.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.*
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import java.math.BigInteger

@RestController
@CrossOrigin
@RequestMapping("/balance")
@Api(description = "Контроллер для проверки баланса")
class BalanceController {

    @ApiOperation("Баланс токенов")
    @GetMapping("token/balance")
    @Throws(Exception::class)
    fun tokenBalance(@ApiParam(value = "Приватный ключ", example = "0") @RequestParam privateKey: BigInteger,
                     @ApiParam(value = "Публичный ключ", example = "0") @RequestParam publicKey: BigInteger): BigInteger {
        val credentials = Credentials.create(ECKeyPair(privateKey, publicKey))
        val web3j = Web3j.build(HttpService("https://ropsten.infura.io/" + credentials.address))
        val contractAddress = "0x9C4D9f2A4cc37cF86386E8859dA613ED8240D365"
        val function = Function(
                "balanceOf",
                listOf<Type<*>>(Address(credentials.address)), // Parameters to pass as Solidity Types
                listOf<TypeReference<*>>(object : TypeReference<Uint256>() {

                }))
        val response = web3j.ethCall(
                Transaction.createEthCallTransaction(credentials.address, contractAddress, FunctionEncoder.encode(function)),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get()
        val result = FunctionReturnDecoder.decode(
                response.value, function.outputParameters)
        val uint = result[0] as Uint256
        return uint.value
    }

    @ApiOperation("Баланс ETH")
    @GetMapping("/balance")
    @Throws(Exception::class)
    fun balance(@ApiParam(value = "Приватный ключ", example = "0") @RequestParam privateKey: BigInteger,
                @ApiParam(value = "Публичный ключ", example = "0") @RequestParam publicKey: BigInteger): BigInteger {
        val credentials = Credentials.create(ECKeyPair(privateKey, publicKey))
        val web3j = Web3j.build(HttpService("https://ropsten.infura.io/" + credentials.address))
        return web3j
                .ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST)
                .send()
                .balance.divide(BigInteger.TEN.pow(18))
    }

    @ApiOperation("Баланс счёта с комиссией")
    @GetMapping("home/balance")
    @Throws(Exception::class)
    fun homeBalance(@ApiParam(value = "Приватный ключ", example = "0") @RequestParam privateKey: BigInteger,
                    @ApiParam(value = "Публичный ключ", example = "0") @RequestParam publicKey: BigInteger): BigInteger {
        val credentials = Credentials.create(ECKeyPair(privateKey, publicKey))
        val web3j = Web3j.build(HttpService("https://ropsten.infura.io/" + credentials.address))
        return web3j
                .ethGetBalance(homeWallet, DefaultBlockParameterName.LATEST)
                .send()
                .balance.divide(BigInteger.TEN.pow(18))
    }

    companion object {

        private const val homeWallet = "0xC140a50f85D72d930202672caB4B17eae3c6EC0b"
    }


}
