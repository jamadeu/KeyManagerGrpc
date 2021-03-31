package br.com.zup.itau

import br.com.zup.TipoConta

data class ContaItauResponse(
    val tipoConta: TipoConta,
    val instituicao: Instituicao,
    val agencia: String,
    val cliente: Cliente
) {
    fun toConta(): Conta {
        return Conta(
            cliente.nome,
            cliente.cpf,
            instituicao
        )
    }
}
