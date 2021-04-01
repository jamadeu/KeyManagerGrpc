package br.com.zup.itau

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.Embeddable
import javax.persistence.Embedded

@Embeddable
data class Conta (
    @JsonProperty(value = "nome")
    val nomeCliente: String?,
    val cpfCliente: String?,
    @Embedded val instituicao: Instituicao
)
