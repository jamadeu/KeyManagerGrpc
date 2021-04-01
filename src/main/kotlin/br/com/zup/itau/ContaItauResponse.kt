package br.com.zup.itau

import br.com.zup.TipoConta

data class ContaItauResponse(
    val tipo: TipoConta,
    val instituicao: Instituicao,
    val agencia: String,
    val titular: Cliente
) {
    fun toConta(): Conta {
        return Conta(
            titular.nome,
            titular.cpf,
            instituicao
        )
    }
}
