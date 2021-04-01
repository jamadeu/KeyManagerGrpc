package br.com.zup.itau

import com.fasterxml.jackson.annotation.JsonProperty

data class Cliente(
    @JsonProperty(value = "id")
    val idCliente: String,
    val nome: String,
    val cpf: String
) {}
