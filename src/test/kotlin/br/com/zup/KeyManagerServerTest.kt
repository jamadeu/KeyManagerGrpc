package br.com.zup

import br.com.zup.TipoChave.*
import br.com.zup.TipoConta.CONTA_CORRENTE
import br.com.zup.itau.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class KeyManagerServerTest(
    private val chavePixRepository: ChavePixRepository,
    private val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: HttpClientItau

    private val idCliente: UUID = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        chavePixRepository.deleteAll()
    }

    @Test
    fun `Registra nova chave pix com CPF`() {
        `when`(itauClient.buscaConta(clienteId = idCliente.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(contaItauResponse(idCliente)))

        grpcClient.registra(
            registraNovaChaveRequest(tipoChave = CPF, "12345678901")
        ).let {
            assertAll(
                Executable { assertEquals(idCliente.toString(), it.idCliente) },
                Executable { assertNotNull(it.idChave) }
            )
            chavePixRepository.findById(it.idChave.toLong()).orElseThrow()
        }.also {
            assertAll(
                Executable { assertEquals("12345678901", it.chave) },
                Executable { assertEquals(CONTA_CORRENTE, it.tipoConta) },
                Executable { assertEquals("UNIBANCO ITAU SA", it.conta.instituicao.nomeInstituicao) },
                Executable { assertEquals("Cliente", it.conta.nomeCliente) }
            )
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["cpfInvalido"])
    fun `Nao registra nova chave pix cpf esta com formato invalido, vazio ou nulo `(cpf: String?) {
        grpcClient.run {
            assertThrows<StatusRuntimeException> {
                registra(registraNovaChaveRequest(tipoChave = CPF, cpf))
            }
        }.also {
            assertTrue(it.message!!.contains("O cpf deve estar no formato 00000000000"))
        }
        if (cpf != null) {
            assertFalse(chavePixRepository.existsByChave(cpf))
        }
    }

    @Test
    fun `Registra nova chave pix com EMAIL`() {
        `when`(itauClient.buscaConta(clienteId = idCliente.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(contaItauResponse(idCliente)))

        grpcClient.registra(
            registraNovaChaveRequest(tipoChave = EMAIL, "email@test.com")
        ).let {
            assertAll(
                Executable { assertEquals(idCliente.toString(), it.idCliente) },
                Executable { assertNotNull(it.idChave) }
            )
            chavePixRepository.findById(it.idChave.toLong()).orElseThrow()
        }.also {
            assertAll(
                Executable { assertEquals("email@test.com", it.chave) },
                Executable { assertEquals(CONTA_CORRENTE, it.tipoConta) },
                Executable { assertEquals("UNIBANCO ITAU SA", it.conta.instituicao.nomeInstituicao) },
                Executable { assertEquals("Cliente", it.conta.nomeCliente) }
            )
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["emailInvalido"])
    fun `Nao registra nova chave pix email esta com formato invalido, vazio ou nulo `(email: String?) {
        grpcClient.run {
            assertThrows<StatusRuntimeException> {
                registra(registraNovaChaveRequest(tipoChave = EMAIL, email))
            }
        }.also {
            assertTrue(it.message!!.contains("O email deve estar no formato email@email.com"))
        }
        if (email != null) {
            assertFalse(chavePixRepository.existsByChave(email))
        }
    }

    @Test
    fun `Registra nova chave pix com CELULAR`() {
        `when`(itauClient.buscaConta(clienteId = idCliente.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(contaItauResponse(idCliente)))

        grpcClient.registra(
            registraNovaChaveRequest(tipoChave = CELULAR, "+5519999999999")
        ).let {
            assertAll(
                Executable { assertEquals(idCliente.toString(), it.idCliente) },
                Executable { assertNotNull(it.idChave) }
            )
            chavePixRepository.findById(it.idChave.toLong()).orElseThrow()
        }.also {
            assertAll(
                Executable { assertEquals("+5519999999999", it.chave) },
                Executable { assertEquals(CONTA_CORRENTE, it.tipoConta) },
                Executable { assertEquals("UNIBANCO ITAU SA", it.conta.instituicao.nomeInstituicao) },
                Executable { assertEquals("Cliente", it.conta.nomeCliente) }
            )
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["emailInvalido"])
    fun `Nao registra nova chave pix celular esta com formato invalido, vazio ou nulo `(celular: String?) {
        grpcClient.run {
            assertThrows<StatusRuntimeException> {
                registra(registraNovaChaveRequest(tipoChave = CELULAR, celular))
            }
        }.also {
            assertTrue(it.message!!.contains("O celular deve estar no formato +5585988714077"))
        }

        if (celular != null) {
            assertFalse(chavePixRepository.existsByChave(celular))
        }
    }

    @Test
    fun `Registra nova chave pix com ALEATORIA`() {
        `when`(itauClient.buscaConta(clienteId = idCliente.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(contaItauResponse(idCliente)))

        grpcClient.registra(
            registraNovaChaveRequest(tipoChave = ALEATORIA, chave = null)
        ).let {
            assertAll(
                Executable { assertEquals(idCliente.toString(), it.idCliente) },
                Executable { assertNotNull(it.idChave) }
            )
            chavePixRepository.findById(it.idChave.toLong()).orElseThrow()
        }.also {
            assertAll(
                Executable { assertNotNull(it.chave) },
                Executable { assertEquals(CONTA_CORRENTE, it.tipoConta) },
                Executable { assertEquals("UNIBANCO ITAU SA", it.conta.instituicao.nomeInstituicao) },
                Executable { assertEquals("Cliente", it.conta.nomeCliente) }
            )
        }
    }

    @Test
    fun `Nao registra nova chave pix quando o tipo da chave for ALEATORIA e a chave informada nao for nula`() {
        grpcClient.run {
            assertThrows<StatusRuntimeException> {
                registra(registraNovaChaveRequest(tipoChave = ALEATORIA, "chave"))
            }
        }.also {
            assertTrue(it.message!!.contains("A chave ira ser gerada automaticamente"))
        }
        assertFalse(chavePixRepository.existsByChave("chave"))

    }

    @Test
    fun `Remove chave`() {
        `when`(itauClient.consultaCliente(clienteId = idCliente.toString()))
            .thenReturn(HttpResponse.ok(contaItauResponse(idCliente)))

        val pix = chavePixRepository.save(chavePix())
        grpcClient.run {
            remove(removeChaveRequest(pix.id.toString(), idCliente = idCliente.toString()))
        }.also {
            assertEquals("Chave removida", it.mensagem)
        }
        assertTrue(chavePixRepository.findById(pix.id!!).isEmpty)
    }

    @Test
    fun `Nao remove chave quando o idCliente e diferente do id do proprietario da chave`() {
        `when`(itauClient.consultaCliente(clienteId = idCliente.toString()))
            .thenReturn(HttpResponse.ok(contaItauResponse(idCliente)))

        val pix = chavePixRepository.save(chavePix())
        grpcClient.run {
            assertThrows<StatusRuntimeException> {
                remove(removeChaveRequest(pix.id.toString(), idCliente = "outroId"))
            }
        }.also {
            assertTrue(it.message!!.contains("Somente o proprietario da chave pode remove-la"))
        }
        assertTrue(chavePixRepository.findById(pix.id!!).isPresent)
    }

    @Test
    fun `Nao remove quando a chave nao existe`() {
        `when`(itauClient.consultaCliente(clienteId = idCliente.toString()))
            .thenReturn(HttpResponse.ok(contaItauResponse(idCliente)))

        grpcClient.run {
            assertThrows<StatusRuntimeException> {
                remove(removeChaveRequest("1", idCliente = idCliente.toString()))
            }
        }.also {
            assertAll(
                Executable { assertTrue(it.message!!.contains("Chave nao localizada")) },
                Executable { assertEquals(Status.NOT_FOUND.code, it.status.code) }
            )
        }
    }


    private fun chavePix() = ChavePix(
        "chave",
        CONTA_CORRENTE,
        idClient = idCliente.toString(),
        Conta(
            "Cliente",
            "00000000000",
            Instituicao(
                "UNIBANCO ITAU SA",
                "60701190"
            )
        )
    )

    private fun removeChaveRequest(idChave: String?, idCliente: String?): RemoveChaveRequest {
        return RemoveChaveRequest.newBuilder()
            .setIdCliente(idCliente)
            .setIdChave(idChave)
            .build()
    }

    private fun registraNovaChaveRequest(tipoChave: TipoChave, chave: String?): RegistraNovaChaveRequest {
        return if (chave == null) {
            RegistraNovaChaveRequest.newBuilder()
                .setIdCliente(idCliente.toString())
                .setTipoChave(tipoChave)
                .setTipoConta(CONTA_CORRENTE)
                .build()
        } else {
            RegistraNovaChaveRequest.newBuilder()
                .setIdCliente(idCliente.toString())
                .setTipoChave(tipoChave)
                .setChave(chave)
                .setTipoConta(CONTA_CORRENTE)
                .build()
        }
    }

    private fun contaItauResponse(idCliente: UUID) = ContaItauResponse(
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

