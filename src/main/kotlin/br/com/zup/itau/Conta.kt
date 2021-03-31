package br.com.zup.itau

import javax.persistence.Embeddable
import javax.persistence.Embedded

@Embeddable
data class Conta (
    val nomeCliente: String?,
    val cpfCliente: String?,
    @Embedded val instituicao: Instituicao
)
