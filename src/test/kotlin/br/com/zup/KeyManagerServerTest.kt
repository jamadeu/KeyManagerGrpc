package br.com.zup

import br.com.zup.TipoConta.CONTA_CORRENTE
import br.com.zup.itau.Cliente
import br.com.zup.itau.ContaItauResponse
import br.com.zup.itau.HttpClientItau
import br.com.zup.itau.Instituicao
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class KeyManagerServerTest(
    private val chavePixRepository: ChavePixRepository,
    private val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: HttpClientItau

    @BeforeEach
    fun setup() {
        chavePixRepository.deleteAll()
    }

    @Test
    fun `Registra nova chave pix`() {
        val idCliente = UUID.randomUUID()
        `when`(itauClient.buscaConta(clienteId = idCliente.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(
                HttpResponse.ok(
                    ContaItauResponse(
                        CONTA_CORRENTE,
                        Instituicao(
                            "UNIBANCO ITAU SA",
                            "60701190"
                        ),
                        "1234",
                        Cliente(
                            idCliente = idCliente.toString(),
                            "Cliente",
                            "00000000000"
                        )
                    )
                )
            )

        grpcClient.registra(
            RegistraNovaChaveRequest.newBuilder()
                .setIdCliente(idCliente.toString())
                .setTipoChave(TipoChave.CPF)
                .setChave("12345678901")
                .setTipoConta(CONTA_CORRENTE)
                .build()
        ).also {
            Assertions.assertAll(
                Executable { assertEquals(idCliente.toString(), it.idCliente) },
                Executable { assertNotNull(it.idChave) }
            )
        }
    }

    @MockBean(HttpClientItau::class)
    fun itauClient(): HttpClientItau {
        return mock(HttpClientItau::class.java)
    }

    @Factory
    class Client {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub? {
            return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}

