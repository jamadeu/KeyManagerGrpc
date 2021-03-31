package br.com.zup.itau

import javax.persistence.Embeddable

@Embeddable
data class Instituicao (
    val nomeInstituicao: String,
    val ispb: String
)
