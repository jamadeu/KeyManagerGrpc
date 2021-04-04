package br.com.zup.shared.validadores

import br.com.zup.NovaChaveRequest
import br.com.zup.TipoChave.*
import io.micronaut.validation.validator.constraints.EmailValidator

fun validaChave(novaChave: NovaChaveRequest?) {
    novaChave?.let {
        when (it.tipoChave) {
            UNKNOWN_TIPO_CHAVE -> {
                throw IllegalArgumentException("O tipo da chave deve ser cpf, email, celular ou aleatoria")
            }

            CPF -> {
                if (it.chave.isNullOrBlank() || !it.chave.matches("^[0-9]{11}\$".toRegex())) {
                    throw IllegalArgumentException("O cpf deve estar no formato 00000000000")
                }
            }

            CELULAR -> {
                if (it.chave.isNullOrBlank() || !it.chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) {
                    throw IllegalArgumentException("O celular deve estar no formato +5585988714077")
                }
            }

            EMAIL -> {
                if (it.chave.isNullOrBlank() || !EmailValidator().isValid(it.chave, null)) {
                    throw IllegalArgumentException("O email deve estar no formato email@email.com")
                }
            }

            ALEATORIA -> {
                if (!it.chave.isNullOrBlank()) {
                    throw IllegalArgumentException("A chave ira ser gerada automaticamente")
                }
            }
            else -> {
                throw IllegalArgumentException("O tipo da chave deve ser cpf, email, celular ou aleatoria")
            }
        }
    }
}