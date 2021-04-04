package br.com.zup

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class RemoveChavePixRequest(
    @field:NotNull val idChave: Long?,
    @field:NotBlank val idCliente: String?
)
