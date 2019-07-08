package com.wsd.glebus.ethereumapi.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.*
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import javax.transaction.Transactional

@RestController
@CrossOrigin
@RequestMapping("/transfer")
@Api(description = "Контроллер для трансфера эфира/токенов")
class TransferController {

    @PostMapping("/send")
    @Transactional
    @ApiOperation("Перевод средств")
    @Throws(Exception::class)
    fun send(@ApiParam("Адресат") @RequestParam to: String,
             @ApiParam(value = "Сумма", example = "0") @RequestParam amount: BigInteger,
             @ApiParam(value = "Приватный ключ", example = "0") @RequestParam privateKey: BigInteger,
             @ApiParam(value = "Публичный ключ", example = "0") @RequestParam publicKey: BigInteger) {
        send("0x9C4D9f2A4cc37cF86386E8859dA613ED8240D365", to, amount, privateKey, publicKey)
    }

    @PostMapping("tokens/send")
    @Transactional
    @ApiOperation("Перевод токенов")
    @Throws(Exception::class)
    fun send(@ApiParam("Адрес контракта") @RequestParam contractAddress: String,
             @ApiParam("Адресат") @RequestParam to: String,
             @ApiParam("Сумма") @RequestParam amount: BigInteger,
             @ApiParam(value = "Приватный ключ", example = "0") @RequestParam privateKey: BigInteger,
             @ApiParam(value = "Публичный ключ", example = "0") @RequestParam publicKey: BigInteger) {
        val credentials = Credentials.create(ECKeyPair(privateKey, publicKey))
        val web3j = Web3j.build(HttpService("https://ropsten.infura.io/" + credentials.address))
        val manager = RawTransactionManager(web3j, credentials)
        val data = encodeTransferData(to, amount)
        val gasPrice = web3j.ethGasPrice().send().gasPrice
        val gasLimit = BigInteger.valueOf(120000) // set gas limit here
        // comission
        Transfer.sendFunds(
                web3j,
                credentials,
                homeWallet,
                BigDecimal.ONE.divide(BigDecimal.TEN, RoundingMode.CEILING),
                Convert.Unit.ETHER)
                .send()
        // transaction
        manager.sendTransaction(gasPrice, gasLimit, contractAddress, data, BigInteger.ZERO)
    }

    companion object {

        private const val homeWallet = "0xC140a50f85D72d930202672caB4B17eae3c6EC0b"

        private fun encodeTransferData(toAddress: String,
                                       sum: BigInteger): String {
            val function = Function(
                    "transfer", // function we're calling
                    listOf<Type<*>>(Address(toAddress), Uint256(sum)), // Parameters to pass as Solidity Types
                    listOf<TypeReference<*>>(object : TypeReference<Bool>() {
                    }))
            return FunctionEncoder.encode(function)
        }
    }


}
