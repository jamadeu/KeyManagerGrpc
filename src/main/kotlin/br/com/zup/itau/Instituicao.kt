package br.com.zup.itau

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.Embeddable

@Embeddable
data class Instituicao(
    @JsonProperty(value = "nome")
    val nomeInstituicao: String,
    val ispb: String
)
