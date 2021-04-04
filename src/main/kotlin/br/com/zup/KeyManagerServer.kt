package br.com.zup

import br.com.zup.shared.exceptions.ChaveNaoExisteException
import br.com.zup.shared.exceptions.ChavePixJaExisteException
import br.com.zup.shared.validadores.validaChave
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyManagerServer(
    @Inject private val chaveService: ChaveService
) : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {
    private val logger = LoggerFactory.getLogger(KeyManagerServer::class.java)
    override fun registra(
        request: RegistraNovaChaveRequest?, responseObserver: StreamObserver<RegistraNovaChaveResponse>?
    ) {
        logger.info("Request nova chave: $request")

        try {
            val chavePix = chaveService.registra(
                request.let {
                    it ?: throw IllegalArgumentException("Request invalida")
                }.run {
                    toNovaChaveRequest()
                }.also {
                    validaChave(it)
                }
            )
            logger.info("Nova chave $chavePix")

            val response = RegistraNovaChaveResponse.newBuilder()
                .setIdCliente(chavePix.idClient)
                .setIdChave(chavePix.id.toString())
                .build()

            logger.info("Response $response")
            responseObserver!!.onNext(response)
            responseObserver.onCompleted()
        } catch (e: ChavePixJaExisteException) {
            logger.error("Chave ${request!!.chave} ja existe")
            val error = Status.ALREADY_EXISTS
                .withDescription(e.message)
                .asRuntimeException()
            responseObserver?.onError(error)
        } catch (e: Exception) {
            logger.error("Something wrong, exception: $e")
            val error = Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .asRuntimeException()
            responseObserver?.onError(error)
        }
    }

    override fun remove(request: RemoveChaveRequest?, responseObserver: StreamObserver<RemoveChaveResponse>?) {
        logger.info("Nova request para remover chave $request")

        try {
            chaveService.remove(
                request.let {
                    it ?: throw IllegalArgumentException("Request invalida")
                }.run {
                    toRemoveChavePixRequest()
                }
            )
            logger.info("Chave removida")

            val response = RemoveChaveResponse.newBuilder()
                .setMensagem("Chave removida")
                .build()

            logger.info("Response $response")
            responseObserver!!.onNext(response)
            responseObserver.onCompleted()
        } catch (e: ChaveNaoExisteException) {
            logger.error("Chave ${request!!.idChave} nao localizada")
            val error = Status.NOT_FOUND
                .withDescription(e.message)
                .asRuntimeException()
            responseObserver?.onError(error)
        } catch (e: Exception) {
            logger.error("Exception: $e")
            val error = Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .asRuntimeException()
            responseObserver?.onError(error)
        }

    }
}


fun RemoveChaveRequest.toRemoveChavePixRequest(): RemoveChavePixRequest {
    return RemoveChavePixRequest(
        this.idChave.toLong(),
        this.idCliente
    )
}

fun RegistraNovaChaveRequest.toNovaChaveRequest(): NovaChaveRequest {
    return NovaChaveRequest(
        this.chave,
        this.tipoChave,
        this.tipoConta,
        this.idCliente
    )
}