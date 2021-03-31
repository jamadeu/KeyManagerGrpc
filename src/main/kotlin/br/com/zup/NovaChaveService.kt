package br.com.zup

import br.com.zup.itau.HttpClientItau
import br.com.zup.shared.exceptions.ChavePixJaExisteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.validation.Valid

@Validated
class NovaChaveService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val httpClientItau: HttpClientItau
) {
    private val logger = LoggerFactory.getLogger(NovaChaveService::class.java)

    fun registra(@Valid novaChave: NovaChaveRequest): ChavePix {
        logger.info("Registering new key $novaChave")

        novaChave.chave?.let {
            if (chavePixRepository.existsByChave(it)) {
                logger.error("Key already exists $it")
                throw ChavePixJaExisteException("Chave $it ja existe")
            }
        }

        logger.info("Call Itau API")
        val findAccountByClientId = httpClientItau.buscaConta(novaChave.idCliente!!, novaChave.tipoChave.toString())
        println(findAccountByClientId.body)
//        logger.info("Account $account returned from the Itau API")

//        val key = newKeyPix.toKey(account = account)
//        keyPixRepository.save(key)
//        logger.info("Registered key: $key")
        val conta = findAccountByClientId.body().toConta()
        return novaChave.toChavePix(conta)
    }
}
