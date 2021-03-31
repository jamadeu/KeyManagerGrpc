package br.com.zup

import br.com.zup.itau.Conta
import io.micronaut.core.annotation.Introspected
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class NovaChaveRequest(
    @field:Size(max = 77)
    val chave: String?,

    @field:NotBlank
    val tipoChave: TipoChave?,

    @field:NotBlank
    val tipoConta: TipoConta?,

    @field:NotBlank
    val idCliente: String?
) {
    private val logger = LoggerFactory.getLogger(NovaChaveRequest::class.java)

    fun toChavePix(conta: Conta): ChavePix {
        return ChavePix(
            chave = chave ?: UUID.randomUUID().toString(),
            tipoConta,
            idCliente,
            conta
        )
    }
}
