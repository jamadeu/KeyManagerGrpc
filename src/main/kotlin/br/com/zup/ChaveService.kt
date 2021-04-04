package br.com.zup

import br.com.zup.itau.HttpClientItau
import br.com.zup.shared.exceptions.ChaveNaoExisteException
import br.com.zup.shared.exceptions.ChavePixJaExisteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChaveService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val httpClientItau: HttpClientItau
) {
    private val logger = LoggerFactory.getLogger(ChaveService::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChaveRequest): ChavePix {
        logger.info("Registrando nova chave $novaChave")

        novaChave.chave?.let {
            if (chavePixRepository.existsByChave(it)) {
                logger.error("Chave $it já existe")
                throw ChavePixJaExisteException("Chave $it ja existe")
            }
        }

        logger.info("API Itau")
        val itauResponse = httpClientItau.buscaConta(
            novaChave.idCliente ?: throw IllegalStateException("idCliente nao pode ser nulo"),
            novaChave.tipoConta?.toString() ?: throw IllegalStateException("tipoConta nao pode ser nulo")
        )
        val conta = itauResponse.body()?.toConta() ?: throw IllegalStateException("Conta nao encontrada")
        logger.info("Conta: $conta")

        val chavePix = novaChave.toChavePix(conta)
        chavePixRepository.save(chavePix)
        logger.info("Chave $chavePix registrada")

        return chavePix
    }

    @Transactional
    fun remove(@Valid request: RemoveChavePixRequest) {
        logger.info("Removendo chave $request")

        logger.info("API Itau")
        val itauResponse = httpClientItau.consultaCliente(
            request.idCliente ?: throw IllegalStateException("idCliente nao pode ser nulo")
        )

        val chavePix = chavePixRepository.findById(
            request.idChave ?: throw IllegalStateException("idChave nao pode ser nulo")
        ).orElseThrow { throw  ChaveNaoExisteException("Chave nao localizada") }

        if(!chavePix.idClient.equals(request.idCliente)){
            throw IllegalStateException("Somente o proprietario da chave pode remove-la")
        }

        chavePixRepository.deleteById(chavePix.id!!)
        logger.info("Chave removida $chavePix")
    }
}
