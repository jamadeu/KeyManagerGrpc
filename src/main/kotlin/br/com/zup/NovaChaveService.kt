package br.com.zup

import br.com.zup.itau.HttpClientItau
import br.com.zup.shared.exceptions.ChavePixJaExisteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class NovaChaveService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val httpClientItau: HttpClientItau
) {
    private val logger = LoggerFactory.getLogger(NovaChaveService::class.java)

    fun registra(@Valid novaChave: NovaChaveRequest): ChavePix {
        logger.info("Registering new key $novaChave")

        novaChave.chave?.let {
            if (chavePixRepository.existsByChave(it)) {
                logger.error("Chave $it já existe")
                throw ChavePixJaExisteException("Chave $it ja existe")
            }
        }

        novaChave.idCliente ?: throw IllegalStateException("idCliente nao pode ser nulo")
        novaChave.tipoConta ?: throw IllegalStateException("tipoConta nao pode ser nulo")

        logger.info("API Itau")
        val itauResponse = httpClientItau.buscaConta(novaChave.idCliente, novaChave.tipoConta)
        val conta = itauResponse.body()?.toConta() ?: throw IllegalStateException("Conta nao encontrada")
        logger.info("Conta: $conta")

        val chavePix = novaChave.toChavePix(conta)
        chavePixRepository.save(chavePix)
        logger.info("Chave $chavePix registrada")

        return chavePix
    }
}
