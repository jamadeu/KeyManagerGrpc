package br.com.zup

import br.com.zup.shared.exceptions.ChavePixJaExisteException
import br.com.zup.shared.validadores.validaChave
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyManagerServer(
    @Inject private val novaChaveService: NovaChaveService
) : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {
    private val logger = LoggerFactory.getLogger(KeyManagerServer::class.java)
    override fun registra(
        request: RegistraNovaChaveRequest?,
        responseObserver: StreamObserver<RegistraNovaChaveResponse>?
    ) {
        logger.info("Request nova chave: $request")

        try {
            val chavePix = novaChaveService.registra(
                request.let {
                    it ?: throw IllegalArgumentException("Invalid request")
                }.run {
                    toNovaChaveRequest()
                }.also {
                    validaChave(it)
                }
            )
            logger.info("New key $chavePix")

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
}

fun RegistraNovaChaveRequest.toNovaChaveRequest(): NovaChaveRequest {
    return NovaChaveRequest(
        this.chave,
        this.tipoChave,
        this.tipoConta,
        this.idCliente
    )
}