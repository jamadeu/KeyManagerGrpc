package br.com.zup.shared.validadores

import br.com.zup.NovaChaveRequest
import br.com.zup.TipoChave.*

fun validaChave(novaChave: NovaChaveRequest?) {
    novaChave?.let {
        when (it.tipoChave) {
            UNKNOWN_TIPO_CHAVE -> {
                throw IllegalArgumentException("The key type must be cpf, email, phone or random")
            }

            CPF -> {
                if (it.chave.isNullOrBlank() || !it.chave.matches("^[0-9]{11}\$".toRegex())) {
                    throw IllegalArgumentException("Cpf must be in the format 00000000000")
                }
            }

            CELULAR -> {
                if (it.chave.isNullOrBlank() || !it.chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) {
                    throw IllegalArgumentException("Phone must be in the format +5585988714077")
                }
            }

            EMAIL -> {
                if (it.chave.isNullOrBlank() || !it.chave.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+\\.([a-z]+)?\$".toRegex())) {
                    throw IllegalArgumentException("Email must be in the format email@email.com")
                }
            }

            ALEATORIA -> {
                if (!it.chave.isNullOrBlank()) {
                    throw IllegalArgumentException("The key will be automatically generated")
                }
            }
            else -> {
                throw IllegalArgumentException("The key type must be cpf, email, phone or random")
            }
        }
    }
}